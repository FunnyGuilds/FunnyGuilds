package net.dzikoysk.funnyguilds.event.guild.member;

import net.dzikoysk.funnyguilds.event.guild.GuildEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public abstract class GuildMemberEvent extends GuildEvent {

    private final Option<User> member;

    public GuildMemberEvent(EventCause eventCause, @Nullable User doer, Guild guild, @Nullable User member) {
        super(eventCause, doer, guild);
        this.member = Option.of(member);
    }

    public Option<User> getMember() {
        return this.member;
    }

}
