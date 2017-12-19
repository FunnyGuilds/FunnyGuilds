package net.dzikoysk.funnyguilds.events.guild.member;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.events.guild.CancellableGuildEvent;
import org.apache.commons.lang3.Validate;

public class CancellableGuildMemberEvent extends CancellableGuildEvent {

    private final User member;

    public CancellableGuildMemberEvent(Guild target, User member) {
        super(target);
        Validate.notNull(member);

        this.member = member;
    }

    public User getMember() {
        return member;
    }

}
