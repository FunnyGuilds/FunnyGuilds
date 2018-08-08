package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;

public class GuildDeleteEvent extends GuildEvent {

    public GuildDeleteEvent(EventCause eventCause, User doer, Guild guild) {
        super(eventCause, doer, guild);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild deletion has been cancelled by the server!";
    }
    
}
