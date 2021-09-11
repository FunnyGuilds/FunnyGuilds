package net.dzikoysk.funnyguilds.user;

import net.dzikoysk.funnyguilds.rank.Rank;

public class UserRank extends Rank {

    private final User user;

    private int points;
    private int kills;
    private int deaths;
    private int assists;
    private int logouts;

    public UserRank(User user, int rankStart) {
        super(user);

        this.user = user;
        this.points = rankStart;
    }

    public User getUser() {
        return user;
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = Math.max(0, points);
        this.entity.markChanged();
    }

    public void addPoints(int change) {
        this.setPoints(getPoints() + change);
    }

    public void removePoints(int change) {
        this.setPoints(getPoints() - change);
    }

    @Override
    public int getKills() {
        return this.kills;
    }

    public void setKills(int kills) {
        this.kills = Math.max(0, kills);
        this.entity.markChanged();
    }

    public void addKills(int change) {
        this.setKills(getKills() + change);
    }

    public void removeKills(int change) {
        this.setKills(getKills() - change);
    }

    @Override
    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = Math.max(0, deaths);
        this.entity.markChanged();
    }

    public void addDeaths(int change) {
        this.setDeaths(getDeaths() + change);
    }

    public void removeDeaths(int change) {
        this.setDeaths(getDeaths() - change);
    }

    @Override
    public int getAssists() {
        return this.assists;
    }

    public void setAssists(int assists) {
        this.assists = Math.max(0, assists);
        this.entity.markChanged();
    }

    public void addAssists(int change) {
        this.setAssists(getAssists() + change);
    }

    public void removeAssists(int change) {
        this.setAssists(getAssists() - change);
    }

    @Override
    public int getLogouts() {
        return this.logouts;
    }

    public void setLogouts(int logouts) {
        this.logouts = Math.max(0, assists);
        this.entity.markChanged();
    }

    public void addLogouts(int change) {
        this.setLogouts(getLogouts() + change);
    }

    public void removeLogouts(int change) {
        this.setLogouts(getLogouts() - change);
    }

    @Override
    public float getKDR() {
        if (getDeaths() == 0) {
            return getKills();
        }

        return 1.0F * getKills() / getDeaths();
    }

}
