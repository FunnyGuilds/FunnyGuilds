package net.dzikoysk.funnyguilds.events.guild;

import net.dzikoysk.funnyguilds.basic.Guild;
import org.bukkit.event.Cancellable;

public class CancellableGuildEvent extends GuildEvent implements Cancellable {

    private boolean cancelled;

    public CancellableGuildEvent(Guild guild) {
        super(guild);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
