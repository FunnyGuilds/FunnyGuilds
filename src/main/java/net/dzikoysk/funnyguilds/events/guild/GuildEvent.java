package net.dzikoysk.funnyguilds.events.guild;

import net.dzikoysk.funnyguilds.basic.Guild;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Guild guild;

    public GuildEvent(Guild guild) {
        Validate.notNull(guild);

        this.guild = guild;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlersList() {
        return handlers;
    }

}
