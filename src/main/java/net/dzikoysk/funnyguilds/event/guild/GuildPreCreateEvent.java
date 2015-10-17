package net.dzikoysk.funnyguilds.event.guild;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildPreCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player owner;
    private final String name;
    private final String tag;
    private boolean cancelled;

    public GuildPreCreateEvent(Player owner, String name, String tag) {
        this.owner = owner;
        this.name = name;
        this.tag = tag;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean f) {
        this.cancelled = f;
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
