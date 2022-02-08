package net.dzikoysk.funnyguilds.guild.top;

import java.util.Comparator;
import net.dzikoysk.funnyguilds.guild.GuildRank;

public final class GuildComparator {

    public static final Comparator<GuildRank> AVG_POINTS_COMPARATOR = new AveragePoints();

    private static class AveragePoints implements Comparator<GuildRank> {

        @Override
        public int compare(GuildRank o1, GuildRank o2) {
            int result = Integer.compare(o1.getAveragePoints(), o2.getAveragePoints());
            if (result == 0) {
                result = compareName(o1, o2);
            }
            return result;
        }

    }

    private static int compareName(GuildRank o1, GuildRank o2) {
        if (o1.getIdentityName() == null) {
            return -1;
        }

        if (o2.getIdentityName() == null) {
            return 1;
        }

        return o1.getIdentityName().compareTo(o2.getIdentityName());
    }

    private GuildComparator() {}

}
