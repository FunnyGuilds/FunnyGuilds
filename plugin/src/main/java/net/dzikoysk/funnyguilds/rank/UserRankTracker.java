package net.dzikoysk.funnyguilds.rank;

import java.util.Optional;
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
                uuid -> Optional.ofNullable(userManager.findByUuid(uuid).orElse(null)),
                isRanked
        );
    }

}
