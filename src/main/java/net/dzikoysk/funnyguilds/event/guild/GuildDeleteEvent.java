package net.dzikoysk.funnyguilds.event.guild;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildDeleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player owner;
    private final String name;
    private final String tag;

    public GuildDeleteEvent(Player owner, String name, String tag) {
        this.owner = owner;
        this.name = name;
        this.tag = tag;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public String getTag() {
        return this.tag;
    }

}
