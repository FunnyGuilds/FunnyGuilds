package net.dzikoysk.funnyguilds.guild.top;

import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.rank.Rank;
import net.dzikoysk.funnyguilds.rank.TopComparator;

public final class GuildComparator {

    public static final TopComparator<GuildRank> POINTS_COMPARATOR = new Points().reversed();
    public static final TopComparator<GuildRank> KILLS_COMPARATOR = new Kills().reversed();
    public static final TopComparator<GuildRank> DEATHS_COMPARATOR = new Deaths().reversed();
    public static final TopComparator<GuildRank> KDR_COMPARATOR = new KDR().reversed();
    public static final TopComparator<GuildRank> ASSISTS_COMPARATOR = new Assists().reversed();
    public static final TopComparator<GuildRank> LOGOUTS_COMPARATOR = new Logouts().reversed();

    public static final TopComparator<GuildRank> AVG_POINTS_COMPARATOR = new AveragePoints().reversed();
    public static final TopComparator<GuildRank> AVG_KILLS_COMPARATOR = new AverageKills().reversed();
    public static final TopComparator<GuildRank> AVG_DEATHS_COMPARATOR = new AverageDeaths().reversed();
    public static final TopComparator<GuildRank> AVG_KDR_COMPARATOR = new AverageKDR().reversed();
    public static final TopComparator<GuildRank> AVG_ASSISTS_COMPARATOR = new AverageAssists().reversed();
    public static final TopComparator<GuildRank> AVG_LOGOUTS_COMPARATOR = new AverageLogouts().reversed();

    private static class Points implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getPoints(), o2.getPoints());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(GuildRank rank) {
            return rank.getPoints();
        }
    }

    private static class Kills implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getKills(), o2.getKills());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(GuildRank rank) {
            return rank.getKills();
        }

    }

    private static class Deaths implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getDeaths(), o2.getDeaths());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(GuildRank rank) {
            return rank.getDeaths();
        }

    }

    private static class KDR implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Float.compare(o1.getKDR(), o2.getKDR());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Float getValue(GuildRank object) {
            return object.getKDR();
        }

    }

    private static class Assists implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAssists(), o2.getAssists());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(GuildRank rank) {
            return rank.getAssists();
        }

    }

    private static class Logouts implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getLogouts(), o2.getLogouts());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(GuildRank rank) {
            return rank.getLogouts();
        }

    }

    private static class AveragePoints implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAveragePoints(), o2.getAveragePoints());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(GuildRank rank) {
            return rank.getAveragePoints();
        }

    }

    private static class AverageKills implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAverageKills(), o2.getAverageKills());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(GuildRank rank) {
            return rank.getAverageKills();
        }

    }

    private static class AverageDeaths implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAverageDeaths(), o2.getAverageDeaths());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(GuildRank rank) {
            return rank.getAverageDeaths();
        }

    }

    private static class AverageKDR implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Float.compare(o1.getAverageKDR(), o2.getAverageKDR());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Float getValue(GuildRank rank) {
            return rank.getAverageKDR();
        }

    }

    private static class AverageAssists implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAverageAssists(), o2.getAverageAssists());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(GuildRank rank) {
            return rank.getAverageAssists();
        }

    }

    private static class AverageLogouts implements TopComparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAverageLogouts(), o2.getAverageLogouts());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

        @Override
        public Integer getValue(GuildRank rank) {
            return rank.getAverageLogouts();
        }

    }

    private GuildComparator() {}

}
