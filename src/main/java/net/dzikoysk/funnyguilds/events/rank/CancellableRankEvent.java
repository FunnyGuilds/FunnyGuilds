package net.dzikoysk.funnyguilds.events.rank;

import net.dzikoysk.funnyguilds.basic.Rank;
import org.bukkit.event.Cancellable;

public class CancellableRankEvent extends RankEvent implements Cancellable {

    private boolean cancelled;

    public CancellableRankEvent(Rank old, Rank actual) {
        super(old, actual);
    }

    @Override
    public final boolean isCancelled() {
        return cancelled;
    }

    @Override
    public final void setCancelled(boolean b) {
        this.cancelled = b;
    }

}
