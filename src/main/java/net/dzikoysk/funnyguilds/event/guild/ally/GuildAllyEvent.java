package net.dzikoysk.funnyguilds.event.guild.ally;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.event.guild.GuildEvent;

public abstract class GuildAllyEvent extends GuildEvent {

    private final Guild alliedGuild;

    public GuildAllyEvent(EventCause eventCause, User doer, Guild guild, Guild alliedGuild) {
        super(eventCause, doer, guild);
        
        this.alliedGuild = alliedGuild;
    }

    public Guild getAlliedGuild() {
        return alliedGuild;
    }

}
