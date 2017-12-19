package net.dzikoysk.funnyguilds.events.rank;

import net.dzikoysk.funnyguilds.basic.Rank;
import net.dzikoysk.funnyguilds.basic.User;
import org.apache.commons.lang3.Validate;

public class RankChangeByAdminEvent extends CancellableRankEvent {

    private final User admin;

    public RankChangeByAdminEvent(Rank old, Rank actual, User admin) {
        super(old, actual);
        Validate.notNull(admin);

        this.admin = admin;
    }

    public User getAdmin() {
        return admin;
    }

}
