package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;

public class GuildUnbanEvent extends GuildEvent {

    public GuildUnbanEvent(EventCause eventCause, User doer, Guild guild) {
        super(eventCause, doer, guild);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild unban has been cancelled by the server!";
    }

}
