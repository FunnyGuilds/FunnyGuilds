package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.rank.Rank;

public class GuildRank extends Rank implements Comparable<GuildRank> {

    private final Guild guild;

    public GuildRank(Guild guild) {
        super(guild);

        this.guild = guild;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public int getPoints() {
        return guild.getMembers().stream()
                .mapToInt(user -> user.getRank().getPoints())
                .sum();
    }

    public int getAveragePoints() {
        return calculateAverage(this.getPoints());
    }

    @Override
    public int getKills() {
        return guild.getMembers().stream()
                .mapToInt(user -> user.getRank().getKills())
                .sum();
    }

    public int getAverageKills() {
        return calculateAverage(this.getKills());
    }

    @Override
    public int getDeaths() {
        return guild.getMembers().stream()
                .mapToInt(user -> user.getRank().getDeaths())
                .sum();
    }

    public int getAverageDeaths() {
        return calculateAverage(this.getDeaths());
    }

    @Override
    public int getAssists() {
        return guild.getMembers().stream()
                .mapToInt(user -> user.getRank().getAssists())
                .sum();
    }

    public int getAverageAssists() {
        return calculateAverage(this.getAssists());
    }

    @Override
    public int getLogouts() {
        return guild.getMembers().stream()
                .mapToInt(user -> user.getRank().getLogouts())
                .sum();
    }

    public int getAverageLogouts() {
        return calculateAverage(this.getLogouts());
    }

    @Override
    public float getKDR() {
        return getDeaths() == 0 ? getKills() : 1.0F * getKills() / getDeaths();
    }

    public float getAverageKDR() {
        return (float) guild.getMembers().stream()
                .mapToDouble(user -> user.getRank().getKDR())
                .average()
                .orElse(0.0D);
    }

    @Override
    public int compareTo(GuildRank rank) {
        int result = Integer.compare(this.getAveragePoints(), rank.getAveragePoints());

        if (result == 0) {
            if (getIdentityName() == null) {
                return -1;
            }

            if (rank.getIdentityName() == null) {
                return 1;
            }

            result = getIdentityName().compareTo(rank.getIdentityName());
        }

        return result;
    }

    private int calculateAverage(int value) {
        return value / guild.getMembers().size();
    }

    private float calculateAverage(float value) {
        return value / guild.getMembers().size();
    }

}
