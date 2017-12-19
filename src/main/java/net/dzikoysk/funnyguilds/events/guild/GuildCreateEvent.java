package net.dzikoysk.funnyguilds.events.guild;

import net.dzikoysk.funnyguilds.basic.Guild;

public class GuildCreateEvent extends CancellableGuildEvent {

    public GuildCreateEvent(Guild target) {
        super(target);
    }
    
}
