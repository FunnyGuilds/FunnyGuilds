package net.dzikoysk.funnyguilds.util.commons.bukkit;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.bukkit.entity.Player;

public class PingUtils {

    public static int getPing(Player player) {
        int ping = 0;

        if (player == null) {
            return ping;
        }

        try {
            Class<?> craftPlayer = Reflections.getCraftBukkitClass("entity.CraftPlayer");
            Object cp = craftPlayer.cast(player);
            Object handle = craftPlayer.getMethod("getHandle").invoke(cp);
            ping = (int) handle.getClass().getField("ping").get(handle);
        } catch (Exception e) {
            if (FunnyGuildsLogger.exception(e.getCause())) {
                e.printStackTrace();
            }
        }

        return ping;
    }

}
