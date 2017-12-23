package net.dzikoysk.funnyguilds.events.guild.member;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;

public class GuildMemberInviteEvent extends CancellableGuildMemberEvent {

    public GuildMemberInviteEvent(Guild target, User member) {
        super(target, member);
    }

}
