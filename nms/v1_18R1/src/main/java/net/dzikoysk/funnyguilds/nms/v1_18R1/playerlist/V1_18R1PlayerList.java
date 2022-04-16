package net.dzikoysk.funnyguilds.nms.v1_18R1.playerlist;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.world.level.EnumGamemode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class V1_18R1PlayerList implements PlayerList {

    private static final EnumGamemode DEFAULT_GAME_MODE = EnumGamemode.a;
    private static final IChatBaseComponent EMPTY_COMPONENT = IChatBaseComponent.a(PlayerListConstants.EMPTY_COMPONENT_VALUE);

    private final int cellCount;

    private final GameProfile[] profileCache = new GameProfile[PlayerListConstants.DEFAULT_CELL_COUNT];

    private boolean firstPacket = true;

    public V1_18R1PlayerList(int cellCount) {
        this.cellCount = cellCount;
    }

    @Override
    public void send(Player player, String[] playerListCells, String header, String footer, SkinTexture[] cellTextures, int ping) {
        final List<Packet<?>> packets = Lists.newArrayList();
        final List<PacketPlayOutPlayerInfo.PlayerInfoData> addPlayerList = Lists.newArrayList();
        final List<PacketPlayOutPlayerInfo.PlayerInfoData> updatePlayerList = Lists.newArrayList();

        try {
            for (int i = 0; i < this.cellCount; i++) {
                if (this.profileCache[i] == null) {
                    GameProfile gameProfile = new GameProfile(
                            UUID.fromString(String.format(PlayerListConstants.UUID_PATTERN, StringUtils.leftPad(String.valueOf(i), 2, '0'))),
                            " "
                    );

                    SkinTexture texture = cellTextures[i];
                    if (texture != null) {
                        gameProfile.getProperties().removeAll("textures");
                        gameProfile.getProperties().put("textures", texture.getProperty());
                    }

                    this.profileCache[i] = gameProfile;
                }

                String text = playerListCells[i];
                GameProfile gameProfile = this.profileCache[i];
                IChatBaseComponent component = CraftChatMessage.fromStringOrNull(text, false);

                PacketPlayOutPlayerInfo.PlayerInfoData playerInfoData = new PacketPlayOutPlayerInfo.PlayerInfoData(
                        gameProfile,
                        ping,
                        DEFAULT_GAME_MODE,
                        component
                );

                if (this.firstPacket) {
                    addPlayerList.add(playerInfoData);
                }

                updatePlayerList.add(playerInfoData);
            }

            if (this.firstPacket) {
                this.firstPacket = false;
            }

            PacketPlayOutPlayerInfo addPlayerPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.a);
            addPlayerPacket.b().addAll(addPlayerList);
            packets.add(addPlayerPacket);

            PacketPlayOutPlayerInfo updatePlayerPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.d);
            updatePlayerPacket.b().addAll(updatePlayerList);
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

                PacketPlayOutPlayerListHeaderFooter headerFooterPacket = new PacketPlayOutPlayerListHeaderFooter(headerComponent, footerComponent);
                packets.add(headerFooterPacket);
            }

            for (Packet<?> packet : packets) {
                ((CraftPlayer) player).getHandle().b.a(packet);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(String.format("Failed to send PlayerList for player '%s'", player.getName()), ex);
        }
    }
}
