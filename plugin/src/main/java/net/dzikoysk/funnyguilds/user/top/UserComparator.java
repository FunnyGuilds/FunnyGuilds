package net.dzikoysk.funnyguilds.user.top;

import net.dzikoysk.funnyguilds.rank.Rank;
import net.dzikoysk.funnyguilds.rank.TopComparator;
import net.dzikoysk.funnyguilds.user.UserRank;

public final class UserComparator {

    public static final TopComparator<UserRank> POINTS_COMPARATOR = new Points().reversed();
    public static final TopComparator<UserRank> KILLS_COMPARATOR = new Kills().reversed();
    public static final TopComparator<UserRank> DEATHS_COMPARATOR = new Deaths().reversed();
    public static final TopComparator<UserRank> KDR_COMPARATOR = new KDR().reversed();
    public static final TopComparator<UserRank> ASSISTS_COMPARATOR = new Assists().reversed();
    public static final TopComparator<UserRank> LOGOUTS_COMPARATOR = new Logouts().reversed();

    private static class Points implements TopComparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Integer.compare(o1.getPoints(), o2.getPoints());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }


        @Override
        public Integer getValue(UserRank rank) {
            return rank.getPoints();
        }
    }

    private static class Kills implements TopComparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Integer.compare(o1.getKills(), o2.getKills());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }
        
        @Override
        public Integer getValue(UserRank rank) {
            return rank.getKills();
        }

    }

    private static class Deaths implements TopComparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Integer.compare(o1.getDeaths(), o2.getDeaths());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(UserRank rank) {
            return rank.getDeaths();
        }

    }

    private static class KDR implements TopComparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Float.compare(o1.getKDR(), o2.getKDR());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Float getValue(UserRank rank) {
            return rank.getKDR();
        }

    }

    private static class Assists implements TopComparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Integer.compare(o1.getAssists(), o2.getAssists());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(UserRank rank) {
            return rank.getAssists();
        }

    }

    private static class Logouts implements TopComparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Integer.compare(o1.getLogouts(), o2.getLogouts());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(UserRank rank) {
            return rank.getLogouts();
        }

    }

    private UserComparator() {}

}
