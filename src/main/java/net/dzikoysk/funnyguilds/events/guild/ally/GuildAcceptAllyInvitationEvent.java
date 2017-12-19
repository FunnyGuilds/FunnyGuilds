package net.dzikoysk.funnyguilds.events.guild.ally;

import net.dzikoysk.funnyguilds.basic.Guild;

public class GuildAcceptAllyInvitationEvent extends CancellableGuildAllyEvent {

    public GuildAcceptAllyInvitationEvent(Guild guild, Guild alliedGuild) {
        super(guild, alliedGuild);
    }

}
