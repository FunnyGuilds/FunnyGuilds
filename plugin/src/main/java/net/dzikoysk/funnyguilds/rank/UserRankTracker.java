package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRankNameProvider;

final class UserRankTracker<T extends Number> extends CommonRankTracker<User, T> {

    UserRankTracker(
            StatKey<T> statKey,
            UserRankNameProvider nameProvider,
            boolean skipPrivilegedPlayersInRankPositions
    ) {
        super(
                statKey,
                nameProvider,
                user -> !skipPrivilegedPlayersInRankPositions || !user.hasPermission("funnyguilds.ranking.exempt")
        );
    }

}
