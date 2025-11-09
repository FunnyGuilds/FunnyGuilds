package net.dzikoysk.funnyguilds.rank;

import java.util.function.Predicate;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;

final class UserRankTracker<T extends Number> extends CommonRankTracker<User, T> {

    UserRankTracker(
            StatKey<T> statKey,
            UserManager userManager,
            Predicate<User> isRanked
    ) {
        super(
                statKey,
                userManager::findByUuid,
                isRanked
        );
    }

}
