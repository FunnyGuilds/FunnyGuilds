package net.dzikoysk.funnyguilds.event.guild.member;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.event.HandlerList;

public class GuildMemberInviteEvent extends GuildMemberEvent {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public GuildMemberInviteEvent(EventCause eventCause, User doer, Guild guild, User member) {
        super(eventCause, doer, guild, member);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Member invitation sending has been cancelled by the server!";
    }
    
}
