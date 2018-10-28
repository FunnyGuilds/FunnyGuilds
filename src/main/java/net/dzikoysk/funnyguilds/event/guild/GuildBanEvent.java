package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.event.HandlerList;

public class GuildBanEvent extends GuildEvent {

    private final long time;
    private final String reason;
    
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
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
