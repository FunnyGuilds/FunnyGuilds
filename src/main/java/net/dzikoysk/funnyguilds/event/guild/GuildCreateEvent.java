package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.Guild;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Guild guild;

    public GuildCreateEvent(Guild guild) {
        this.guild = guild;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Guild getGuild() {
        return this.guild;
    }

}
