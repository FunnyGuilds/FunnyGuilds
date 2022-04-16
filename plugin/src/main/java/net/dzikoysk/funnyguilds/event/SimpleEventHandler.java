package net.dzikoysk.funnyguilds.event;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;

public class SimpleEventHandler {

    public static boolean handle(FunnyEvent event) {
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled() && FunnyGuilds.getInstance().getPluginConfiguration().debugMode) {
            event.notifyDoer();
            return false;
        }

        return true;
    }

}
