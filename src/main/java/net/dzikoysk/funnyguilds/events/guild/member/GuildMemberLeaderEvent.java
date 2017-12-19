package net.dzikoysk.funnyguilds.events.guild.member;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;

public class GuildMemberLeaderEvent extends CancellableGuildMemberEvent {

    public GuildMemberLeaderEvent(Guild target, User member) {
        super(target, member);
    }

}
