package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.rank.Rank;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.event.FunnyEvent;

public abstract class RankEvent extends FunnyEvent {

    private final Rank rank;

    public RankEvent(EventCause eventCause, User doer, Rank rank) {
        super(eventCause, doer);

        this.rank = rank;
    }

    public Rank getRank() {
        return this.rank;
    }

}
