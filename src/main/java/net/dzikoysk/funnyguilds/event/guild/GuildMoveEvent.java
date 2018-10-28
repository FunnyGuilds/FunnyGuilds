package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

public class GuildMoveEvent extends GuildEvent {

    private final Location newLocation;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
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