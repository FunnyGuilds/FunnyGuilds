package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import org.bukkit.Location;

public class GuildMoveEvent extends GuildEvent {

    private final Location newLocation;
    
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