package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildCreateEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildCreateEvent(EventCause eventCause, User doer, Guild guild) {
        super(eventCause, doer, guild);
    }

    @Override
    public void setCancelled(boolean cancelled) {
        throw new UnsupportedOperationException("GuildCreateEvent cannot be cancelled, use GuildPreCreateEvent");
    }

    @Override
    public String getDefaultCancelMessage() {
        throw new UnsupportedOperationException("GuildCreateEvent cannot be cancelled, use GuildPreCreateEvent");
    }

}
