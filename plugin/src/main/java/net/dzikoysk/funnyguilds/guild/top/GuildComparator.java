package net.dzikoysk.funnyguilds.guild.top;

import java.util.function.Function;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.rank.Rank;
import net.dzikoysk.funnyguilds.rank.TopComparator;

public final class GuildComparator implements TopComparator<GuildRank> {

    public static final TopComparator<GuildRank> POINTS_COMPARATOR = new GuildComparator(GuildRank::getPoints).reversed();
    public static final TopComparator<GuildRank> KILLS_COMPARATOR = new GuildComparator(GuildRank::getKills).reversed();
    public static final TopComparator<GuildRank> DEATHS_COMPARATOR = new GuildComparator(GuildRank::getDeaths).reversed();
    public static final TopComparator<GuildRank> KDR_COMPARATOR = new GuildComparator(GuildRank::getKDR).reversed();
    public static final TopComparator<GuildRank> KDA_COMPARATOR = new GuildComparator(GuildRank::getKDA).reversed();
    public static final TopComparator<GuildRank> ASSISTS_COMPARATOR = new GuildComparator(GuildRank::getAssists).reversed();
    public static final TopComparator<GuildRank> LOGOUTS_COMPARATOR = new GuildComparator(GuildRank::getLogouts).reversed();

    public static final TopComparator<GuildRank> AVG_POINTS_COMPARATOR = new GuildComparator(GuildRank::getAveragePoints).reversed();
    public static final TopComparator<GuildRank> AVG_KILLS_COMPARATOR = new GuildComparator(GuildRank::getAverageKills).reversed();
    public static final TopComparator<GuildRank> AVG_DEATHS_COMPARATOR = new GuildComparator(GuildRank::getAverageDeaths).reversed();
    public static final TopComparator<GuildRank> AVG_KDR_COMPARATOR = new GuildComparator(GuildRank::getAverageKDR).reversed();
    public static final TopComparator<GuildRank> AVG_KDA_COMPARATOR = new GuildComparator(GuildRank::getAverageKDA).reversed();
    public static final TopComparator<GuildRank> AVG_ASSISTS_COMPARATOR = new GuildComparator(GuildRank::getAverageAssists).reversed();
    public static final TopComparator<GuildRank> AVG_LOGOUTS_COMPARATOR = new GuildComparator(GuildRank::getAverageLogouts).reversed();

    private final Function<GuildRank, Number> valueFunction;

    private GuildComparator(Function<GuildRank, Number> valueFunction) {
        this.valueFunction = valueFunction;
    }

    @Override
    public int compare(GuildRank o1, GuildRank o2) {
        int result = Float.compare(this.getValue(o1).floatValue(), this.getValue(o2).floatValue());
        if (result == 0) {
            result = Rank.compareName(o1, o2);
        }

        return result;
    }

    @Override
    public Number getValue(GuildRank rank) {
        return this.valueFunction.apply(rank);
    }

}
