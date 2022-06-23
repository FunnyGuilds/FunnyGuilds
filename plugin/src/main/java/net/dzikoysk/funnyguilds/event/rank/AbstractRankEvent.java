package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.user.User;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRankEvent extends FunnyEvent {

    private final User affected;

    public AbstractRankEvent(EventCause eventCause, @Nullable User doer, User affected) {
        super(eventCause, doer);
        this.affected = affected;
    }

    /**
     * @return the user that rank has been changed
     */
    public User getAffected() {
        return this.affected;
    }

}
