package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildBaseChangeEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Location newBaseLocation;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildBaseChangeEvent(EventCause eventCause, User doer, Guild guild, Location newBaseLocation) {
        super(eventCause, doer, guild);

        this.newBaseLocation = newBaseLocation;
    }

    public Location getNewBaseLocation() {
        return this.newBaseLocation;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild base location change has been cancelled by the server!";
    }

}