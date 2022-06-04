package net.dzikoysk.funnyguilds.nms.api.statistics;

import org.bukkit.entity.Player;

public interface StatisticsAccessor {

    double getTpsInLastMinute();

    int getReloadCount();

    int getPlayerPing(Player player);

}
