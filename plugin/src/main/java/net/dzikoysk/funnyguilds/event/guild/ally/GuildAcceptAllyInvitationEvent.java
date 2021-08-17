package net.dzikoysk.funnyguilds.event.guild.ally;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class GuildAcceptAllyInvitationEvent extends GuildAllyEvent {

    private static final HandlerList handlers = new HandlerList();
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public GuildAcceptAllyInvitationEvent(EventCause eventCause, User doer, Guild guild, Guild alliedGuild) {
        super(eventCause, doer, guild, alliedGuild);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Alliance acceptance has been cancelled by the server!";
    }

}
