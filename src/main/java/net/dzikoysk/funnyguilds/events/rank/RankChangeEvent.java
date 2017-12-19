package net.dzikoysk.funnyguilds.events.rank;

import net.dzikoysk.funnyguilds.basic.Rank;
import net.dzikoysk.funnyguilds.basic.User;
import org.apache.commons.lang3.Validate;

public class RankChangeEvent extends CancellableRankEvent {

    private final User killer;

    public RankChangeEvent(Rank old, Rank actual, User killer) {
        super(old, actual);
        Validate.notNull(killer);

        this.killer = killer;
    }

    public User getKiller() {
        return killer;
    }

}
