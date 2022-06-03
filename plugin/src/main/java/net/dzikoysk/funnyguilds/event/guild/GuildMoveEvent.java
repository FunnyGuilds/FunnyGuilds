package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildMoveEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Location newLocation;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildMoveEvent(EventCause eventCause, User doer, Guild guild, Location newLocation) {
        super(eventCause, doer, guild);
        this.newLocation = newLocation;
    }

    public Location getNewLocation() {
        return this.newLocation;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild region location change has been cancelled by the server!";
    }

}