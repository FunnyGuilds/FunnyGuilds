package net.dzikoysk.funnyguilds.nms.v1_16R3.playerlist;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.nms.api.ProtocolDependentHelper;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class V1_16R3PlayerList implements PlayerList {

    private static final IChatBaseComponent EMPTY_COMPONENT = IChatBaseComponent.ChatSerializer.a(PlayerListConstants.EMPTY_COMPONENT_VALUE);
    private static final PlayerInfoDataHelper PLAYER_INFO_DATA_HELPER = new PlayerInfoDataHelper(PacketPlayOutPlayerInfo.class, EnumGamemode.SURVIVAL);
    private static final Field PLAYER_INFO_DATA_ACCESSOR;

    static {
        try {
            PLAYER_INFO_DATA_ACCESSOR = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
            PLAYER_INFO_DATA_ACCESSOR.setAccessible(true);
        }
        catch (NoSuchFieldException exception) {
            throw new RuntimeException("Could not initialize V1_16R3PlayerList", exception);
        }
    }

    private final int cellCount;
    private final GameProfile[] profileCache = new GameProfile[PlayerListConstants.DEFAULT_CELL_COUNT];
    private boolean firstPacket = true;

    public V1_16R3PlayerList(int cellCount) {
        this.cellCount = cellCount;
    }

    @Override
    public void send(Player player, String[] playerListCells, String header, String footer, SkinTexture[] cellTextures,
                     int ping, Set<Integer> forceUpdateSlots) {
        List<Packet<?>> packets = Lists.newArrayList();
        List<Object> addPlayerList = Lists.newArrayList();
        List<Object> updatePlayerList = Lists.newArrayList();

        try {
            PacketPlayOutPlayerInfo addPlayerPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER);
            PacketPlayOutPlayerInfo updatePlayerPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_DISPLAY_NAME);

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
                IChatBaseComponent component = CraftChatMessage.fromString(text, false)[0];

                if (this.firstPacket || forceUpdateSlots.contains(i)) {
                    SkinTexture texture = cellTextures[i];
                    if (texture != null) {
                        gameProfile.getProperties().removeAll("textures");
                        gameProfile.getProperties().put("textures", texture.getProperty());
                    }
                }

                Object playerInfoData = PLAYER_INFO_DATA_HELPER.createPlayerInfoData(
                        addPlayerPacket,
                        gameProfile,
                        ping,
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

            PLAYER_INFO_DATA_ACCESSOR.set(addPlayerPacket, addPlayerList);
            packets.add(addPlayerPacket);

            PLAYER_INFO_DATA_ACCESSOR.set(updatePlayerPacket, updatePlayerList);
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

                PacketPlayOutPlayerListHeaderFooter headerFooterPacket = new PacketPlayOutPlayerListHeaderFooter();
                headerFooterPacket.header = headerComponent;
                headerFooterPacket.footer = footerComponent;
                packets.add(headerFooterPacket);
            }

            for (Packet<?> packet : packets) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to send PlayerList for player " + player.getName(), exception);
        }
    }

}
