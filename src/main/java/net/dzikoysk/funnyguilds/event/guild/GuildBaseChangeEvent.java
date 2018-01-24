package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import org.bukkit.Location;

public class GuildBaseChangeEvent extends GuildEvent {

    private final Location newBaseLocation;
    
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