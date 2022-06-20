package net.dzikoysk.funnyguilds.event.guild.ally;

import net.dzikoysk.funnyguilds.event.guild.GuildEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.jetbrains.annotations.Nullable;

public abstract class GuildAllyEvent extends GuildEvent {

    private final Guild alliedGuild;

    public GuildAllyEvent(EventCause eventCause, @Nullable User doer, Guild guild, Guild alliedGuild) {
        super(eventCause, doer, guild);
        this.alliedGuild = alliedGuild;
    }

    public Guild getAlliedGuild() {
        return this.alliedGuild;
    }

}
