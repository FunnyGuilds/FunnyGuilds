package net.dzikoysk.funnyguilds.guild.top;

import java.util.Comparator;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.rank.Rank;

public final class GuildComparator {

    public static final Comparator<GuildRank> POINTS_COMPARATOR = new Points().reversed();
    public static final Comparator<GuildRank> KILLS_COMPARATOR = new Kills().reversed();
    public static final Comparator<GuildRank> DEATHS_COMPARATOR = new Deaths().reversed();
    public static final Comparator<GuildRank> KDR_COMPARATOR = new KDR().reversed();
    public static final Comparator<GuildRank> ASSISTS_COMPARATOR = new Assists().reversed();
    public static final Comparator<GuildRank> LOGOUTS_COMPARATOR = new Logouts().reversed();

    public static final Comparator<GuildRank> AVG_POINTS_COMPARATOR = new AveragePoints().reversed();
    public static final Comparator<GuildRank> AVG_KILLS_COMPARATOR = new AverageKills().reversed();
    public static final Comparator<GuildRank> AVG_DEATHS_COMPARATOR = new AverageDeaths().reversed();
    public static final Comparator<GuildRank> AVG_KDR_COMPARATOR = new AverageKDR().reversed();
    public static final Comparator<GuildRank> AVG_ASSISTS_COMPARATOR = new AverageAssists().reversed();
    public static final Comparator<GuildRank> AVG_LOGOUTS_COMPARATOR = new AverageLogouts().reversed();

    private static class Points implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getPoints(), o2.getPoints());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class Kills implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getKills(), o2.getKills());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class Deaths implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getDeaths(), o2.getDeaths());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class KDR implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Float.compare(o1.getKDR(), o2.getKDR());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class Assists implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAssists(), o2.getAssists());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class Logouts implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getLogouts(), o2.getLogouts());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class AveragePoints implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAveragePoints(), o2.getAveragePoints());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class AverageKills implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAverageKills(), o2.getAverageKills());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class AverageDeaths implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAverageDeaths(), o2.getAverageDeaths());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class AverageKDR implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Float.compare(o1.getAverageKDR(), o2.getAverageKDR());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class AverageAssists implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAverageAssists(), o2.getAverageAssists());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private static class AverageLogouts implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAverageLogouts(), o2.getAverageLogouts());
            if (result == 0) {
                result = Rank.compareName(o1, o2);
            }
            return result;
        }

    }

    private GuildComparator() {}

}
