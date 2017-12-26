package net.dzikoysk.funnyguilds.event.guild.ally;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;

public class GuildSendAllyInvitationEvent extends GuildAllyEvent {

    public GuildSendAllyInvitationEvent(EventCause eventCause, User doer, Guild guild, Guild alliedGuild) {
        super(eventCause, doer, guild, alliedGuild);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Alliance invatation sending has been cancelled by the server!";
    }
    
}
