package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.guild.top.GuildComparator;
import net.dzikoysk.funnyguilds.rank.Rank;
import org.jetbrains.annotations.NotNull;

public class GuildRank extends Rank<Guild> implements Comparable<GuildRank> {

    GuildRank(Guild guild) {
        super(guild);
    }

    public Guild getGuild() {
        return this.entity;
    }

    @Override
    public int getPoints() {
        return this.entity.getMembers().stream()
                .mapToInt(user -> user.getRank().getPoints())
                .sum();
    }

    public int getAveragePoints() {
        return this.calculateAverage(this.getPoints());
    }

    @Override
    public int getKills() {
        return this.entity.getMembers().stream()
                .mapToInt(user -> user.getRank().getKills())
                .sum();
    }

    public int getAverageKills() {
        return this.calculateAverage(this.getKills());
    }

    @Override
    public int getDeaths() {
        return this.entity.getMembers().stream()
                .mapToInt(user -> user.getRank().getDeaths())
                .sum();
    }

    public int getAverageDeaths() {
        return this.calculateAverage(this.getDeaths());
    }

    @Override
    public int getAssists() {
        return this.entity.getMembers().stream()
                .mapToInt(user -> user.getRank().getAssists())
                .sum();
    }

    public int getAverageAssists() {
        return this.calculateAverage(this.getAssists());
    }

    @Override
    public int getLogouts() {
        return this.entity.getMembers().stream()
                .mapToInt(user -> user.getRank().getLogouts())
                .sum();
    }

    public int getAverageLogouts() {
        return this.calculateAverage(this.getLogouts());
    }

    @Override
    public float getKDR() {
        return this.getDeaths() == 0
                ? this.getKills()
                : 1.0F * this.getKills() / this.getDeaths();
    }

    public float getAverageKDR() {
        return (float) this.entity.getMembers().stream()
                .mapToDouble(user -> user.getRank().getKDR())
                .average()
                .orElse(0.0D);
    }

    @Override
    public float getKDA() {
        return this.getDeaths() == 0
                ? this.getKills() + this.getAssists()
                : 1.0F * (this.getKills() + this.getAssists()) / this.getDeaths();
    }

    public float getAverageKDA() {
        return (float) this.entity.getMembers().stream()
                .mapToDouble(user -> user.getRank().getKDA())
                .average()
                .orElse(0.0D);
    }

    @Override
    public int compareTo(@NotNull GuildRank rank) {
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
