package net.dzikoysk.funnyguilds.event.guild.member;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;

public class GuildMemberAcceptInviteEvent extends GuildMemberEvent {

    public GuildMemberAcceptInviteEvent(EventCause eventCause, User doer, Guild guild, User member) {
        super(eventCause, doer, guild, member);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Member invitation acceptance has been cancelled by the server!";
    }
    
}
