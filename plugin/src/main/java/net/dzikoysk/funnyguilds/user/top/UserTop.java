package net.dzikoysk.funnyguilds.user.top;

import java.util.NavigableSet;
import java.util.function.BiFunction;
import net.dzikoysk.funnyguilds.rank.Top;
import net.dzikoysk.funnyguilds.rank.TopComparator;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import panda.std.Option;

public class UserTop extends Top<UserRank> {

    public UserTop(TopComparator<UserRank> comparator, BiFunction<String, TopComparator<UserRank>, NavigableSet<UserRank>> recalculateFunction) {
        super(comparator, recalculateFunction);
    }

    public Option<User> getUser(int place) {
        return this.get(place)
                .map(UserRank::getUser);
    }

}
