package net.dzikoysk.funnyguilds.events.guild.ally;

import net.dzikoysk.funnyguilds.basic.Guild;

public class GuildSendAllyInvitationEvent extends CancellableGuildAllyEvent {

    public GuildSendAllyInvitationEvent(Guild guild, Guild alliedGuild) {
        super(guild, alliedGuild);
    }

}
