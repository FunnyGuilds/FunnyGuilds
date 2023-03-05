package net.dzikoysk.funnyguilds.nms.v1_19R2.playerlist;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.nms.api.ProtocolDependentHelper;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.a;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.world.level.EnumGamemode;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class V1_19R2PlayerList implements PlayerList {

    private static final EnumGamemode DEFAULT_GAME_MODE = EnumGamemode.a;
    private static final IChatBaseComponent EMPTY_COMPONENT = CraftChatMessage.fromString("", false)[0];

    private static final Field playerInfoEntriesField;

    static {
        try {
            playerInfoEntriesField = ClientboundPlayerInfoUpdatePacket.class.getDeclaredField("b");
            playerInfoEntriesField.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            throw new IllegalStateException("missing 'b' field in ClientboundPlayerInfoUpdatePacket", ex);
        }
    }

    private final int cellCount;
    private final GameProfile[] profileCache = new GameProfile[PlayerListConstants.DEFAULT_CELL_COUNT];
    private boolean firstPacket = true;

    public V1_19R2PlayerList(int cellCount) {
        this.cellCount = cellCount;
    }

    @Override
    public void send(Player player, String[] playerListCells, String header, String footer, SkinTexture[] cellTextures, int ping,
                     Set<Integer> forceUpdateSlots) {
        List<Packet<?>> packets = Lists.newArrayList();
        List<ClientboundPlayerInfoUpdatePacket.b> addPlayerList = Lists.newArrayList();
        List<ClientboundPlayerInfoUpdatePacket.b> updatePlayerList = Lists.newArrayList();

        try {
            for (int i = 0; i < this.cellCount; i++) {
                String paddedIdentifier = StringUtils.leftPad(String.valueOf(i), 2, '0');
                String gameProfileName = ProtocolDependentHelper.getGameProfileNameBasedOnPlayerProtocolVersion(player, paddedIdentifier, paddedIdentifier);

                if (this.profileCache[i] == null) {
                    this.profileCache[i] = new GameProfile(
                            UUID.fromString(String.format(PlayerListConstants.UUID_PATTERN, paddedIdentifier)),
                            gameProfileName
                    );
                }

                String text = playerListCells[i];
                GameProfile gameProfile = this.profileCache[i];
                IChatBaseComponent component = CraftChatMessage.fromString(text, false)[0];

                if (this.firstPacket || forceUpdateSlots.contains(i)) {
                    SkinTexture texture = cellTextures[i];
                    if (texture != null) {
                        gameProfile.getProperties().removeAll("textures");
                        gameProfile.getProperties().put("textures", texture.getProperty());
                    }
                }

                ClientboundPlayerInfoUpdatePacket.b playerInfoData = new ClientboundPlayerInfoUpdatePacket.b(
                        gameProfile.getId(),
                        gameProfile,
                        true,
                        ping,
                        DEFAULT_GAME_MODE,
                        component,
                        null
                );

                if (this.firstPacket || forceUpdateSlots.contains(i)) {
                    addPlayerList.add(playerInfoData);
                }

                updatePlayerList.add(playerInfoData);
            }

            if (this.firstPacket) {
                this.firstPacket = false;
            }

            ClientboundPlayerInfoUpdatePacket addPlayerPacket = createPlayerInfoPacket(
                    EnumSet.of(a.a, a.c, a.d, a.e, a.f), // add player, update gamemode, update listed, update latency, update display name
                    addPlayerList
            );
            packets.add(addPlayerPacket);

            ClientboundPlayerInfoUpdatePacket updatePlayerPacket = createPlayerInfoPacket(
                    EnumSet.of(a.e, a.f), // update latency, update display name
                    updatePlayerList
            );
            packets.add(updatePlayerPacket);

            boolean headerNotEmpty = !header.isEmpty();
            boolean footerNotEmpty = !footer.isEmpty();

            if (headerNotEmpty || footerNotEmpty) {
                IChatBaseComponent headerComponent = EMPTY_COMPONENT;
                IChatBaseComponent footerComponent = EMPTY_COMPONENT;

                if (headerNotEmpty) {
                    headerComponent = CraftChatMessage.fromStringOrNull(header, true);
                }

                if (footerNotEmpty) {
                    footerComponent = CraftChatMessage.fromStringOrNull(footer, true);
                }

                PacketPlayOutPlayerListHeaderFooter headerFooterPacket =
                        new PacketPlayOutPlayerListHeaderFooter(headerComponent, footerComponent);
                packets.add(headerFooterPacket);
            }

            for (Packet<?> packet : packets) {
                ((CraftPlayer) player).getHandle().b.a(packet);
            }
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to send PlayerList for player " + player.getName(), exception);
        }
    }

    private ClientboundPlayerInfoUpdatePacket createPlayerInfoPacket(EnumSet<ClientboundPlayerInfoUpdatePacket.a> actions, List<ClientboundPlayerInfoUpdatePacket.b> entries) {
        // NOTE: this whole hack exists just because Mojang does stupid things and collects list of entries
        //       into an immutable list without any ability to modify or pass direct entries through constructor.
        ClientboundPlayerInfoUpdatePacket playerInfoPacket =
                new ClientboundPlayerInfoUpdatePacket(actions, Collections.emptyList());

        try {
            playerInfoEntriesField.set(playerInfoPacket, entries);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException("could not create player info packet", ex);
        }

        return playerInfoPacket;
    }

}
