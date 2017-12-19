package net.dzikoysk.funnyguilds.events.guild;

import net.dzikoysk.funnyguilds.basic.Guild;
import org.bukkit.event.Cancellable;

public class CancellableGuildEvent extends GuildEvent implements Cancellable {

    private boolean cancelled;

    public CancellableGuildEvent(Guild target) {
        super(target);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

}
