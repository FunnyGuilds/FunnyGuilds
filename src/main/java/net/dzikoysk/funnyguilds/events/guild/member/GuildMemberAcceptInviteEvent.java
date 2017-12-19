package net.dzikoysk.funnyguilds.events.guild.member;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;

public class GuildMemberAcceptInviteEvent extends CancellableGuildMemberEvent {

    public GuildMemberAcceptInviteEvent(Guild target, User member) {
        super(target, member);
    }

}
