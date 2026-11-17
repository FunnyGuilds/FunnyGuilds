package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.rank.GuildStatAggregationType;
import net.dzikoysk.funnyguilds.rank.Rank;
import net.dzikoysk.funnyguilds.rank.StatKey;
import net.dzikoysk.funnyguilds.user.UserStatKeys;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Deprecated
@ApiStatus.ScheduledForRemoval
public class GuildRank extends Rank<Guild> implements Comparable<GuildRank> {

    GuildRank(Guild guild) {
        super(guild);
    }

    public Guild getGuild() {
        return this.entity;
    }

    @Override
    public int getPoints() {
        return this.total(UserStatKeys.POINTS);
    }

    public int getAveragePoints() {
        return this.average(UserStatKeys.POINTS);
    }

    @Override
    public int getKills() {
        return this.total(UserStatKeys.KILLS);
    }

    public int getAverageKills() {
        return this.average(UserStatKeys.KILLS);
    }

    @Override
    public int getDeaths() {
        return this.total(UserStatKeys.DEATHS);
    }

    public int getAverageDeaths() {
        return this.average(UserStatKeys.DEATHS);
    }

    @Override
    public int getAssists() {
        return this.total(UserStatKeys.ASSISTS);
    }

    public int getAverageAssists() {
        return this.average(UserStatKeys.ASSISTS);
    }

    @Override
    public int getLogouts() {
        return this.total(UserStatKeys.LOGOUTS);
    }

    public int getAverageLogouts() {
        return this.average(UserStatKeys.LOGOUTS);
    }

    @Override
    public float getKDR() {
        return this.ratio(UserStatKeys.KDR, GuildStatAggregationType.TOTAL_DOUBLE);
    }

    public float getAverageKDR() {
        return this.ratio(UserStatKeys.KDR, GuildStatAggregationType.AVERAGE);
    }

    @Override
    public float getKDA() {
        return this.ratio(UserStatKeys.KDA, GuildStatAggregationType.TOTAL_DOUBLE);
    }

    public float getAverageKDA() {
        return this.ratio(UserStatKeys.KDA, GuildStatAggregationType.AVERAGE);
    }

    private int total(StatKey<Integer> key) {
        return this.stat(key, GuildStatAggregationType.TOTAL_INT);
    }

    private int average(StatKey<Integer> key) {
        return this.stat(key, GuildStatAggregationType.AVERAGE_INT);
    }

    private float ratio(StatKey<Double> key, GuildStatAggregationType<Double> aggregation) {
        return this.stat(key, aggregation).floatValue();
    }

    private <T extends Number, R extends Number> R stat(StatKey<T> key, GuildStatAggregationType<R> aggregation) {
        return FunnyGuilds.getInstance().getRankService().getGuildStatValue(this.entity, key, aggregation)
                .orElseGet(() -> aggregation.computeValue(FunnyGuilds.getInstance().getRankService(), this.entity, key));
    }

    public int getWarsWon() {
        return this.entity.getWarsWon();
    }

    public int getWarsLost() {
        return this.entity.getWarsLost();
    }

    public int getWarStreak() {
        return this.entity.getWarStreak();
    }

    @Override
    public int compareTo(@NotNull GuildRank rank) {
        return Integer.compare(rank.getAveragePoints(), this.getAveragePoints());
    }

    @Override
    public String toString() {
        return Integer.toString(this.getAveragePoints());
    }
}
