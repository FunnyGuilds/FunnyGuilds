package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;

public class GuildExtendValidityEvent extends GuildEvent {

    private final long extendTime;
    
    public GuildExtendValidityEvent(EventCause eventCause, User doer, Guild guild, long extendTime) {
        super(eventCause, doer, guild);
        
        this.extendTime = extendTime;
    }

    public long getExtendTime() {
        return this.extendTime;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild validity extension has been cancelled by the server!";
    }
    
}
