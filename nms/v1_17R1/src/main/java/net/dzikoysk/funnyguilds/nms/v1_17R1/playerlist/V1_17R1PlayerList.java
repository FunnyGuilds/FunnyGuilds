package net.dzikoysk.funnyguilds.nms.v1_17R1.playerlist;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.v1_8R3.playerlist.PlayerInfoDataHelper;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.world.level.EnumGamemode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class V1_17R1PlayerList implements PlayerList {
    private static final IChatBaseComponent EMPTY_COMPONENT = IChatBaseComponent.a("");
    private static final PlayerInfoDataHelper PLAYER_INFO_DATA_HELPER = new PlayerInfoDataHelper(PacketPlayOutPlayerInfo.class, EnumGamemode.a);

    private final int cellCount;

    private final GameProfile[] profileCache = new GameProfile[PlayerListConstants.DEFAULT_CELL_COUNT];

    private boolean firstPacket = true;

    public V1_17R1PlayerList(int cellCount) {
        this.cellCount = cellCount;
    }

    @Override
    public void send(Player player, String[] playerListCells, String header, String footer, int ping) {
        final List<Packet<?>> packets = Lists.newArrayList();
        final List<Object> addPlayerList = Lists.newArrayList();
        final List<Object> updatePlayerList = Lists.newArrayList();

        try {
            for (int i = 0; i < this.cellCount; i++) {
                if (this.profileCache[i] == null) {
                    this.profileCache[i] = new GameProfile(
                            UUID.fromString(String.format(PlayerListConstants.UUID_PATTERN, StringUtils.leftPad(String.valueOf(i), 2, '0'))),
                            " "
                    );
                }

                String text = playerListCells[i];
                GameProfile gameProfile = this.profileCache[i];
                IChatBaseComponent component = CraftChatMessage.fromStringOrNull(text, false);

                Object playerInfoData = PLAYER_INFO_DATA_HELPER.createPlayerInfoData(
                        new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.a),
                        gameProfile,
                        ping,
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
            addAllWithoutGenericType(addPlayerPacket.b(), addPlayerList);
            packets.add(addPlayerPacket);

            PacketPlayOutPlayerInfo updatePlayerPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.d);
            addAllWithoutGenericType(updatePlayerPacket.b(), updatePlayerList);
            packets.add(updatePlayerPacket);

            IChatBaseComponent headerComponent = EMPTY_COMPONENT;
            IChatBaseComponent footerComponent = EMPTY_COMPONENT;

            if (! header.isEmpty()) {
                headerComponent = CraftChatMessage.fromStringOrNull(header, true);
            }

            if (! footer.isEmpty()) {
                footerComponent = CraftChatMessage.fromStringOrNull(footer, true);
            }

            PacketPlayOutPlayerListHeaderFooter headerFooterPacket = new PacketPlayOutPlayerListHeaderFooter(headerComponent, footerComponent);
            packets.add(headerFooterPacket);

            for (Packet<?> packet : packets) {
                ((CraftPlayer) player).getHandle().b.sendPacket(packet);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(String.format("Failed to send PlayerList for player '%s'", player.getName()), ex);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addAllWithoutGenericType(List list, List objectsToAdd) {
        list.addAll(objectsToAdd);
    }
}
