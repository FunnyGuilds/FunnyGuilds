package net.dzikoysk.funnyguilds.event.guild.member;

import net.dzikoysk.funnyguilds.event.guild.GuildEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.jetbrains.annotations.Nullable;

public abstract class GuildMemberEvent extends GuildEvent {

    private final User member;

    public GuildMemberEvent(EventCause eventCause, @Nullable User doer, Guild guild, User member) {
        super(eventCause, doer, guild);
        this.member = member;
    }

    /**
     * @return the user that event has been fired for
     */
    public User getMember() {
        return this.member;
    }

}
