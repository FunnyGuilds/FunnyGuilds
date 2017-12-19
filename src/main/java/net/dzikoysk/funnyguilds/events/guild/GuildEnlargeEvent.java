package net.dzikoysk.funnyguilds.events.guild;

import net.dzikoysk.funnyguilds.basic.Guild;

public class GuildEnlargeEvent extends CancellableGuildEvent {

    public GuildEnlargeEvent(Guild guild) {
        super(guild);
    }

}
