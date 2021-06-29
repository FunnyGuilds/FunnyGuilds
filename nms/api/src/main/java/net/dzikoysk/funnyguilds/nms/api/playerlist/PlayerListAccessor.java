package net.dzikoysk.funnyguilds.nms.api.playerlist;

import org.bukkit.entity.Player;

public interface PlayerListAccessor {
    PlayerList createPlayerList(Player player, int cellCount);
}
