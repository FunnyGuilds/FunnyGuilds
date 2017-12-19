package net.dzikoysk.funnyguilds.events.guild;

import net.dzikoysk.funnyguilds.basic.Guild;

public class GuildDeleteEvent extends CancellableGuildEvent {

    public GuildDeleteEvent(Guild target) {
        super(target);
    }

}
