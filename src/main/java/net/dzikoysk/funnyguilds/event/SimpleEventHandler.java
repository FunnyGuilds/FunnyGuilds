package net.dzikoysk.funnyguilds.event;

import org.bukkit.Bukkit;

public class SimpleEventHandler {

    public static boolean handle(FunnyEvent event) {
        Bukkit.getPluginManager().callEvent(event);
        
        if (event.isCancelled()) {
            event.notifyDoer();
            return false;
        }
        
        return true;
    }
    
}
