package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;

public class GuildBanEvent extends GuildEvent {

    private final long time;
    private final String reason;
    
    public GuildBanEvent(EventCause eventCause, User doer, Guild guild, long time, String reason) {
        super(eventCause, doer, guild);
        
        this.time = time;
        this.reason = reason;
    }

    public long getTime() {
        return time;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild ban has been cancelled by the server!";
    }
    
}
