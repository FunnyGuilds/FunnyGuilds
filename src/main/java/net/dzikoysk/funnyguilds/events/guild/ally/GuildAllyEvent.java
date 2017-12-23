package net.dzikoysk.funnyguilds.events.guild.ally;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.events.guild.GuildEvent;
import org.apache.commons.lang3.Validate;

public class GuildAllyEvent extends GuildEvent {

    private final Guild alliedGuild;

    public GuildAllyEvent(Guild guild, Guild alliedGuild) {
        super(guild);
        Validate.notNull(alliedGuild);

        this.alliedGuild = alliedGuild;
    }

    public Guild getAlliedGuild() {
        return alliedGuild;
    }

}
