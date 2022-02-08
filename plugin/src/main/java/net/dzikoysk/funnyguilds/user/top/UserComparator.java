package net.dzikoysk.funnyguilds.user.top;

import java.util.Comparator;
import net.dzikoysk.funnyguilds.rank.Rank;
import net.dzikoysk.funnyguilds.user.UserRank;

public final class UserComparator {

    public static final Comparator<UserRank> POINTS_COMPARATOR = new Points().reversed();
    public static final Comparator<UserRank> KILLS_COMPARATOR = new Kills().reversed();
    public static final Comparator<UserRank> DEATHS_COMPARATOR = new Deaths().reversed();
    public static final Comparator<UserRank> KDR_COMPARATOR = new KDR().reversed();
    public static final Comparator<UserRank> ASSISTS_COMPARATOR = new Assists().reversed();
    public static final Comparator<UserRank> LOGOUTS_COMPARATOR = new Logouts().reversed();

    private static class Points implements Comparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Integer.compare(o1.getPoints(), o2.getPoints());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class Kills implements Comparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Integer.compare(o1.getKills(), o2.getKills());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class Deaths implements Comparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Integer.compare(o1.getDeaths(), o2.getDeaths());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class KDR implements Comparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Float.compare(o1.getKDR(), o2.getKDR());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class Assists implements Comparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Integer.compare(o1.getAssists(), o2.getAssists());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class Logouts implements Comparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Integer.compare(o1.getLogouts(), o2.getLogouts());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private UserComparator() {}

}
