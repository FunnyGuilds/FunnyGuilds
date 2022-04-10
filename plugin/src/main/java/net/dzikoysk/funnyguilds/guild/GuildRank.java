package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.guild.top.GuildComparator;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.rank.Rank;
import org.jetbrains.annotations.ApiStatus;

public class GuildRank extends Rank<Guild> implements Comparable<GuildRank> {

    GuildRank(Guild guild) {
        super(guild);
    }

    public Guild getGuild() {
        return entity;
    }

    /**
     * @deprecated for removal in the future, in favour of {@link Rank#getPosition(String)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "4.11.0")
    @Override
    public int getPosition() {
        return this.getPosition(DefaultTops.GUILD_AVG_POINTS_TOP);
    }

    @Override
    public int getPoints() {
        return this.entity.getMembers().stream()
                .mapToInt(user -> user.getRank().getPoints())
                .sum();
    }

    public int getAveragePoints() {
        return calculateAverage(this.getPoints());
    }

    @Override
    public int getKills() {
        return this.entity.getMembers().stream()
                .mapToInt(user -> user.getRank().getKills())
                .sum();
    }

    public int getAverageKills() {
        return calculateAverage(this.getKills());
    }

    @Override
    public int getDeaths() {
        return this.entity.getMembers().stream()
                .mapToInt(user -> user.getRank().getDeaths())
                .sum();
    }

    public int getAverageDeaths() {
        return calculateAverage(this.getDeaths());
    }

    @Override
    public int getAssists() {
        return this.entity.getMembers().stream()
                .mapToInt(user -> user.getRank().getAssists())
                .sum();
    }

    public int getAverageAssists() {
        return calculateAverage(this.getAssists());
    }

    @Override
    public int getLogouts() {
        return this.entity.getMembers().stream()
                .mapToInt(user -> user.getRank().getLogouts())
                .sum();
    }

    public int getAverageLogouts() {
        return calculateAverage(this.getLogouts());
    }

    @Override
    public float getKDR() {
        return getDeaths() == 0
                ? getKills()
                : 1.0F * getKills() / getDeaths();
    }

    public float getAverageKDR() {
        return (float) this.entity.getMembers().stream()
                .mapToDouble(user -> user.getRank().getKDR())
                .average()
                .orElse(0.0D);
    }

    @Override
    public int compareTo(GuildRank rank) {
        return GuildComparator.AVG_POINTS_COMPARATOR.compare(this, rank);
    }

    private int calculateAverage(int value) {
        return value / this.entity.getMembers().size();
    }

    @Override
    public String toString() {
        return Integer.toString(this.getAveragePoints());
    }

}
