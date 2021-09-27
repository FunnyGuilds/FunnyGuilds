package net.dzikoysk.funnyguilds.event.guild.member;

import net.dzikoysk.funnyguilds.event.guild.GuildEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

public abstract class GuildMemberEvent extends GuildEvent {

    private final User member;

    public GuildMemberEvent(EventCause eventCause, User doer, Guild guild, User member) {
        super(eventCause, doer, guild);

        this.member = member;
    }

    public User getMember() {
        return this.member;
    }

}
