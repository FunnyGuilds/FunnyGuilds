package net.dzikoysk.funnyguilds.nms.v1_21_9.statistics;

import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

public class V1_21_9StatisticsAccessor implements StatisticsAccessor {

    @Override
    public double getTpsInLastMinute() {
        return ((CraftServer) Bukkit.getServer()).getServer().getTPS()[0];
    }

    @Override
    public int getReloadCount() {
        return ((CraftServer) Bukkit.getServer()).reloadCount;
    }

    @Override
    public int getPlayerPing(Player player) {
        return player.getPing();
    }

}
