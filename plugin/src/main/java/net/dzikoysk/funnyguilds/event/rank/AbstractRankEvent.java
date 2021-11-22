package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;

public abstract class AbstractRankEvent extends FunnyEvent {

    private final User affected;

    public AbstractRankEvent(EventCause eventCause, User doer, User affected) {
        super(eventCause, doer);
        this.affected = affected;
    }

    public User getAffected() {
        return affected;
    }

    /**
     * @return the rank of affected user
     * @deprecated for removal in the future, in favour of {@link AbstractRankEvent#getAffected()} and then {@link User#getRank()}
     */
    @Deprecated
    public UserRank getRank() {
        return this.affected.getRank();
    }

}
