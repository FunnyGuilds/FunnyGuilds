package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class GuildDeleteEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public GuildDeleteEvent(EventCause eventCause, User doer, Guild guild) {
        super(eventCause, doer, guild);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild deletion has been cancelled by the server!";
    }
    
}
