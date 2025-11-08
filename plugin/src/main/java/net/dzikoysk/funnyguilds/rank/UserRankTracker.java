package net.dzikoysk.funnyguilds.rank;

import java.util.concurrent.Executor;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRankNameProvider;

final class UserRankTracker<T extends Number> extends CommonRankTracker<User, T> {

    UserRankTracker(
            Executor executor,
            StatKey<T> statKey,
            UserRankNameProvider nameProvider,
            boolean skipPrivilegedPlayersInRankPositions
    ) {
        super(
                executor,
                statKey,
                nameProvider,
                user -> !skipPrivilegedPlayersInRankPositions || !user.hasPermission("funnyguilds.ranking.exempt")
        );
    }

}
