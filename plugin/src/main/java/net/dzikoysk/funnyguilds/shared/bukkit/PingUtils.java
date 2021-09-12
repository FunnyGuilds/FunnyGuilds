package net.dzikoysk.funnyguilds.shared.bukkit;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.entity.Player;

public final class PingUtils {

    private PingUtils() {}

    public static int getPing(Player player) {
        return Math.max(0, FunnyGuilds.getInstance().getNmsAccessor().getStatisticsAccessor().getPlayerPing(player));
    }

}
