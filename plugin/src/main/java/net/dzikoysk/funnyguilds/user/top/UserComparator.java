package net.dzikoysk.funnyguilds.user.top;

import java.util.function.Function;
import net.dzikoysk.funnyguilds.rank.Rank;
import net.dzikoysk.funnyguilds.rank.TopComparator;
import net.dzikoysk.funnyguilds.user.UserRank;

public final class UserComparator implements TopComparator<UserRank> {

    public static final TopComparator<UserRank> POINTS_COMPARATOR = new UserComparator(UserRank::getPoints).reversed();
    public static final TopComparator<UserRank> KILLS_COMPARATOR = new UserComparator(UserRank::getKills).reversed();
    public static final TopComparator<UserRank> DEATHS_COMPARATOR = new UserComparator(UserRank::getDeaths).reversed();
    public static final TopComparator<UserRank> KDR_COMPARATOR = new UserComparator(UserRank::getKDR).reversed();
    public static final TopComparator<UserRank> KDA_COMPARATOR = new UserComparator(UserRank::getKDA).reversed();
    public static final TopComparator<UserRank> ASSISTS_COMPARATOR = new UserComparator(UserRank::getAssists).reversed();
    public static final TopComparator<UserRank> LOGOUTS_COMPARATOR = new UserComparator(UserRank::getLogouts).reversed();

    private final Function<UserRank, Number> valueFunction;

    private UserComparator(Function<UserRank, Number> valueFunction) {
        this.valueFunction = valueFunction;
    }

    @Override
    public int compare(UserRank o1, UserRank o2) {
        int result = Float.compare(this.getValue(o1).floatValue(), this.getValue(o2).floatValue());
        if (result == 0) {
            result = Rank.compareName(o1, o2);
        }

        return result;
    }

    @Override
    public Number getValue(UserRank rank) {
        return this.valueFunction.apply(rank);
    }

}
