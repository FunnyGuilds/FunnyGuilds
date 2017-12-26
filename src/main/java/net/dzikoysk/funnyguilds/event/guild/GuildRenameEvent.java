package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;

public class GuildRenameEvent extends GuildEvent {

    private final String newName;
    
    public GuildRenameEvent(EventCause eventCause, User doer, Guild guild, String newName) {
        super(eventCause, doer, guild);
        
        this.newName = newName;
    }

    public String getNewName() {
        return this.newName;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild renaming has been cancelled by the server!";
    }
    
}