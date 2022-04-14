package net.dzikoysk.funnyguilds.nms.v1_13R2.playerlist;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.dzikoysk.funnyguilds.nms.v1_8R3.playerlist.PlayerInfoDataHelper;
import net.minecraft.server.v1_13_R2.EnumGamemode;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerListHeaderFooter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class V1_13R2PlayerList implements PlayerList {

    private static final IChatBaseComponent EMPTY_COMPONENT = IChatBaseComponent.ChatSerializer.a(PlayerListConstants.EMPTY_COMPONENT_VALUE);
    private static final PlayerInfoDataHelper PLAYER_INFO_DATA_HELPER = new PlayerInfoDataHelper(PacketPlayOutPlayerInfo.class, EnumGamemode.SURVIVAL);

    private static final Field PLAYER_INFO_DATA_ACCESSOR;

    static {
        try {
            PLAYER_INFO_DATA_ACCESSOR = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
            PLAYER_INFO_DATA_ACCESSOR.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            throw new RuntimeException(String.format("Could not initialize '%s'", V1_13R2PlayerList.class.getName()), ex);
        }
    }

    private final int cellCount;

    private final GameProfile[] profileCache = new GameProfile[PlayerListConstants.DEFAULT_CELL_COUNT];

    private boolean firstPacket = true;

    public V1_13R2PlayerList(int cellCount) {
        this.cellCount = cellCount;
    }

    @Override
    public void send(Player player, String[] playerListCells, String header, String footer, SkinTexture[] cellTextures, int ping) {
        final List<Packet<?>> packets = Lists.newArrayList();
        final List<Object> addPlayerList = Lists.newArrayList();
        final List<Object> updatePlayerList = Lists.newArrayList();

        try {
            PacketPlayOutPlayerInfo addPlayerPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER);
            PacketPlayOutPlayerInfo updatePlayerPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_DISPLAY_NAME);

            for (int i = 0; i < this.cellCount; i++) {
                if (this.profileCache[i] == null) {
                    this.profileCache[i] = new GameProfile(
                            UUID.fromString(String.format(PlayerListConstants.UUID_PATTERN, StringUtils.leftPad(String.valueOf(i), 2, '0'))),
                            " "
                    );
                }

                String text = playerListCells[i];
                GameProfile gameProfile = this.profileCache[i];

                SkinTexture texture = cellTextures[i];
                if (texture != null) {
                    gameProfile.getProperties().removeAll("textures");
                    gameProfile.getProperties().put("textures", new Property("textures", texture.getValue(), texture.getSignature()));
                }

                IChatBaseComponent component = CraftChatMessage.fromStringOrNull(text, false);

                Object playerInfoData = PLAYER_INFO_DATA_HELPER.createPlayerInfoData(
                        addPlayerPacket,
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
        catch (Exception ex) {
            throw new RuntimeException(String.format("Failed to send PlayerList for player '%s'", player.getName()), ex);
        }
    }
}
