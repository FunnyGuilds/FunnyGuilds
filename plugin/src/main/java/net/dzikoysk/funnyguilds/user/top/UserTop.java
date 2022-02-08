package net.dzikoysk.funnyguilds.user.top;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.rank.Top;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;

public class UserTop extends Top<UserRank> {

    public UserTop(Comparator<UserRank> comparator, Function<Comparator<UserRank>, NavigableSet<UserRank>> recalculateFunction) {
        super(comparator, recalculateFunction);
    }

    public User getUser(int place) {
        return this.get(place)
                .map(UserRank::getUser)
                .getOrNull();
    }

}
