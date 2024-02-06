package net.dzikoysk.funnyguilds.nms.v1_18R2.playerlist;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.dzikoysk.funnyguilds.nms.api.ProtocolDependentHelper;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket.Action;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket.PlayerUpdate;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.world.level.GameType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class V1_18R2PlayerList implements PlayerList {

    private static final GameType DEFAULT_GAME_MODE = GameType.SURVIVAL;
    private static final Component EMPTY_COMPONENT = CraftChatMessage.fromString("", false)[0];

    private final int cellCount;
    private final GameProfile[] profileCache = new GameProfile[PlayerListConstants.DEFAULT_CELL_COUNT];
    private boolean firstPacket = true;

    public V1_18R2PlayerList(int cellCount) {
        this.cellCount = cellCount;
    }

    @Override
    public void send(Player player, String[] playerListCells, String header, String footer, SkinTexture[] cellTextures,
                     int ping, Set<Integer> forceUpdateSlots) {
        List<Packet<?>> packets = Lists.newArrayList();
        List<PlayerUpdate> addPlayerList = Lists.newArrayList();
        List<PlayerUpdate> updatePlayerList = Lists.newArrayList();

        try {
            for (int i = 0; i < this.cellCount; i++) {
                String paddedIdentifier = StringUtils.leftPad(String.valueOf(i), 2, '0');
                String gameProfileName = ProtocolDependentHelper.getGameProfileNameBasedOnPlayerProtocolVersion(player, paddedIdentifier);

                if (this.profileCache[i] == null) {
                    this.profileCache[i] = new GameProfile(
                            UUID.fromString(String.format(PlayerListConstants.UUID_PATTERN, paddedIdentifier)),
                            gameProfileName
                    );
                }

                String text = playerListCells[i];
                GameProfile gameProfile = this.profileCache[i];
                Component component = CraftChatMessage.fromString(text, false)[0];

                if (this.firstPacket || forceUpdateSlots.contains(i)) {
                    SkinTexture texture = cellTextures[i];
                    if (texture != null) {
                        gameProfile.getProperties().removeAll("textures");
                        gameProfile.getProperties().put("textures", texture.getProperty());
                    }
                }

                PlayerUpdate playerInfoData = new PlayerUpdate(
                        gameProfile,
                        ping,
                        DEFAULT_GAME_MODE,
                        component
                );

                if (this.firstPacket || forceUpdateSlots.contains(i)) {
                    addPlayerList.add(playerInfoData);
                }

                updatePlayerList.add(playerInfoData);
            }

            if (this.firstPacket) {
                this.firstPacket = false;
            }

            ClientboundPlayerInfoPacket addPlayerPacket = new ClientboundPlayerInfoPacket(Action.ADD_PLAYER);
            addPlayerPacket.getEntries().addAll(addPlayerList);
            packets.add(addPlayerPacket);

            ClientboundPlayerInfoPacket updatePlayerPacket = new ClientboundPlayerInfoPacket(Action.UPDATE_DISPLAY_NAME);
            updatePlayerPacket.getEntries().addAll(updatePlayerList);
            packets.add(updatePlayerPacket);

            boolean headerNotEmpty = !header.isEmpty();
            boolean footerNotEmpty = !footer.isEmpty();

            if (headerNotEmpty || footerNotEmpty) {
                Component headerComponent = EMPTY_COMPONENT;
                Component footerComponent = EMPTY_COMPONENT;

                if (headerNotEmpty) {
                    headerComponent = CraftChatMessage.fromStringOrNull(header, true);
                }

                if (footerNotEmpty) {
                    footerComponent = CraftChatMessage.fromStringOrNull(footer, true);
                }

                ClientboundTabListPacket headerFooterPacket = new ClientboundTabListPacket(headerComponent, footerComponent);
                packets.add(headerFooterPacket);
            }

            for (Packet<?> packet : packets) {
                ((CraftPlayer) player).getHandle().connection.send(packet);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send PlayerList for player " + player.getName(), exception);
        }
    }
}
