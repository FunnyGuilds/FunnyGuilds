package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.rank.Rank;

public class GuildRank extends Rank {

    private final Guild guild;

    public GuildRank(Guild guild) {
        super(guild);

        this.guild = guild;
    }

    public Guild getGuild() {
        return guild;
    }

    /* Points */
    @Override
    public int getPoints() {
        return guild.getMembers()
                .stream().mapToInt(user -> user.getRank().getPoints()).sum();
    }

    public int getAveragePoints() {
        return calculateAverage(this.getPoints());
    }

    /* Kills */
    @Override
    public int getKills() {
        return guild.getMembers()
                .stream().mapToInt(user -> user.getRank().getKills()).sum();
    }

    public int getAverageKills() {
        return calculateAverage(this.getKills());
    }

    /* Deaths */
    @Override
    public int getDeaths() {
        return guild.getMembers()
                .stream().mapToInt(user -> user.getRank().getDeaths()).sum();
    }

    public int getAverageDeaths() {
        return calculateAverage(this.getDeaths());
    }

    /* Assists */
    @Override
    public int getAssists() {
        return guild.getMembers()
                .stream().mapToInt(user -> user.getRank().getAssists()).sum();
    }

    public int getAverageAssists() {
        return calculateAverage(this.getAssists());
    }

    /* Logouts */
    @Override
    public int getLogouts() {
        return guild.getMembers()
                .stream().mapToInt(user -> user.getRank().getLogouts()).sum();
    }

    public int getAverageLogouts() {
        return calculateAverage(this.getLogouts());
    }

    /* KDR */
    @Override
    public float getKDR() {
        if (getDeaths() == 0) {
            return getKills();
        }

        return 1.0F * getKills() / getDeaths();
    }

    public float getAverageKDR() {
        float kdr = (float) guild.getMembers()
                .stream().mapToDouble(user -> user.getRank().getKDR()).sum();

        return calculateAverage(kdr);
    }

    /* Compare */

    @Override
    public int compareTo(Rank rank) {
        if(!(rank instanceof GuildRank)) {
            return -1;
        }
        GuildRank guildRank = (GuildRank) rank;

        int result = Integer.compare(this.getAveragePoints(), guildRank.getAveragePoints());

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

    /*
        Utility methods
     */
    private int calculateAverage(int value) {
        return value / guild.getMembers().size();
    }

    private float calculateAverage(float value) {
        return value / guild.getMembers().size();
    }

}
