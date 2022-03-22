package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;

@FunctionalInterface
public interface UserResolver {
    Object resolve(User user);

    interface RankResolver extends UserResolver {
        Object resolve(User user, UserRank rank);

        @Override
        default Object resolve(User user) {
            return this.resolve(user, user.getRank());
        }
    }

}
