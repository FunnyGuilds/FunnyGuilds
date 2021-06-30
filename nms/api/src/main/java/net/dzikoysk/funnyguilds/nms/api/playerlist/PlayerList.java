package net.dzikoysk.funnyguilds.nms.api.playerlist;

import org.bukkit.entity.Player;

public interface PlayerList {
    void send(Player player, String[] playerListCells, String header, String footer, int ping);
}
