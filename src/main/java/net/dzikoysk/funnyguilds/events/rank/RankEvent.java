package net.dzikoysk.funnyguilds.events.rank;

import net.dzikoysk.funnyguilds.basic.Rank;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RankEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Rank old;
    private final Rank actual;

    public RankEvent(Rank old, Rank actual) {
        Validate.notNull(old);
        Validate.notNull(actual);

        this.old = old;
        this.actual = actual;
    }

    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }

    public Rank getOld() {
        return old;
    }

    public Rank getActual() {
        return actual;
    }

    public static HandlerList getHandlersList() {
        return handlers;
    }

}
