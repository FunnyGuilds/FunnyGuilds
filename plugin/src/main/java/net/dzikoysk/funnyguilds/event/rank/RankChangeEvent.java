package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.rank.Rank;
import net.dzikoysk.funnyguilds.user.User;

public abstract class RankChangeEvent extends RankEvent {

    private int change;

    public RankChangeEvent(EventCause eventCause, Rank rank, User doer, int change) {
        super(eventCause, doer, rank);

        this.change = change;
    }

    public int getChange() {
        return this.change;
    }

    public void setChange(int change) {
        this.change = change;
    }

}
