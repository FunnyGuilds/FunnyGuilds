package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

public abstract class GuildEvent extends FunnyEvent {

    private final Guild guild;

    public GuildEvent(EventCause eventCause, @Nullable User doer, Guild guild) {
        super(eventCause, doer, !Bukkit.isPrimaryThread());
        this.guild = guild;
    }

    public GuildEvent(EventCause eventCause, @Nullable User doer, Guild guild, boolean isAsync) {
        super(eventCause, doer, isAsync);
        this.guild = guild;
    }

    /**
     * @return the guild that event has been fired for
     */
    public Guild getGuild() {
        return this.guild;
    }

}
