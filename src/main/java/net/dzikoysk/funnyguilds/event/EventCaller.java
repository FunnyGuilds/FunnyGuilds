package net.dzikoysk.funnyguilds.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class EventCaller {

    public static <E extends Event> E callEvent(E event) {
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

}
