package net.dzikoysk.funnyguilds.nms.v26_1_2.playerlist;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.papermc.paper.adventure.AdventureComponent;
import io.papermc.paper.profile.MutablePropertyMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.world.level.GameType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V26_1_2PlayerList implements PlayerList {

    private static final GameType DEFAULT_GAME_MODE = GameType.SURVIVAL;
    // base chosen randomly to override other entries
    private static final int PRIORITY_BASE = 999;

    private final int cellCount;
    private final GameProfile[] profileCache = new GameProfile[PlayerListConstants.DEFAULT_CELL_COUNT];
    private boolean firstPacket = true;

    public V26_1_2PlayerList(int cellCount) {
        this.cellCount = cellCount;
    }

    @Override
    public void send(Player player, net.kyori.adventure.text.Component[] playerListCells, net.kyori.adventure.text.Component header, net.kyori.adventure.text.Component footer, SkinTexture[] cellTextures, int ping,
                     Set<Integer> forceUpdateSlots) {
        List<Packet<?>> packets = Lists.newArrayList();
        List<Entry> addPlayerList = Lists.newArrayList();
        List<Entry> updatePlayerList = Lists.newArrayList();

        try {
            for (int i = 0; i < this.cellCount; i++) {
                String paddedIdentifier = StringUtils.leftPad(String.valueOf(i), 2, '0');
                String gameProfileName = " ";

                if (this.profileCache[i] == null) {
                    this.profileCache[i] = new GameProfile(
                            UUID.fromString(String.format(PlayerListConstants.UUID_PATTERN, paddedIdentifier)),
                            gameProfileName,
                            new MutablePropertyMap()
                    );
                }

                net.kyori.adventure.text.Component text = playerListCells[i];
                GameProfile gameProfile = this.profileCache[i];
                Component component = new AdventureComponent(text);

                if (this.firstPacket || forceUpdateSlots.contains(i)) {
                    SkinTexture texture = cellTextures[i];
                    if (texture != null) {
                        gameProfile.properties().removeAll("textures");
                        gameProfile.properties().put("textures", texture.getProperty());
                    }
                }

                // higher priority first
                int cellPriority = PRIORITY_BASE - i;
                Entry playerInfoData = new Entry(
                        gameProfile.id(),
                        gameProfile,
                        true,
                        ping,
                        DEFAULT_GAME_MODE,
                        component,
                        false,
                        cellPriority,
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
                    EnumSet.of(
                            Action.ADD_PLAYER,
                            Action.UPDATE_GAME_MODE,
                            Action.UPDATE_LISTED,
                            Action.UPDATE_LATENCY,
                            Action.UPDATE_DISPLAY_NAME,
                            Action.UPDATE_LIST_ORDER
                    ),
                    addPlayerList
            );
            packets.add(addPlayerPacket);

            ClientboundPlayerInfoUpdatePacket updatePlayerPacket = createPlayerInfoPacket(
                    EnumSet.of(Action.UPDATE_LATENCY, Action.UPDATE_DISPLAY_NAME),
                    updatePlayerList
            );
            packets.add(updatePlayerPacket);

            Component headerComponent = new AdventureComponent(header);
            Component footerComponent = new AdventureComponent(footer);

            ClientboundTabListPacket headerFooterPacket =
                    new ClientboundTabListPacket(
                            headerComponent,
                            footerComponent
                    );
            packets.add(headerFooterPacket);

            for (Packet<?> packet : packets) {
                ((CraftPlayer) player).getHandle().connection.send(packet);
            }
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to send PlayerList for player " + player.getName(), exception);
        }
    }

    private ClientboundPlayerInfoUpdatePacket createPlayerInfoPacket(
            EnumSet<Action> actions,
            List<Entry> entries
    ) {
        return new ClientboundPlayerInfoUpdatePacket(
                actions,
                entries
        );
    }
}
