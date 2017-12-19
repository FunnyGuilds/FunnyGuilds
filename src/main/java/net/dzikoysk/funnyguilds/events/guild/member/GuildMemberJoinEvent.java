package net.dzikoysk.funnyguilds.events.guild.member;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;

public class GuildMemberJoinEvent extends CancellableGuildMemberEvent {

    public GuildMemberJoinEvent(Guild target, User member) {
        super(target, member);
    }
    
}
