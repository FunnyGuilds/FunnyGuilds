package net.dzikoysk.funnyguilds.events.guild.ally;

import net.dzikoysk.funnyguilds.basic.Guild;

public class GuildBreakAllyEvent extends CancellableGuildAllyEvent {

    public GuildBreakAllyEvent(Guild guild, Guild alliedGuild) {
        super(guild, alliedGuild);
    }

}
