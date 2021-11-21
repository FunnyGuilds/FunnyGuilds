package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.user.User;

public abstract class RankChangeEvent extends RankEvent {

    protected int change;

    public RankChangeEvent(EventCause eventCause, User doer, User affected, int change) {
        super(eventCause, doer, affected);

        this.change = change;
    }

    public int getChange() {
        return this.change;
    }

    public void setChange(int change) {
        this.change = change;
    }

}
