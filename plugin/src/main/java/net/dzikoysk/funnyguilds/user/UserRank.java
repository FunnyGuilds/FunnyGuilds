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
        this.points = points;
        this.entity.markChanged();
    }

    public void addPoints(int change) {
        this.setPoints(Math.max(0, getPoints() + change));
    }

    public void removePoints(int change) {
        this.setPoints(Math.max(0, getPoints() - change));
    }

    @Override
    public int getKills() {
        return this.kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        this.entity.markChanged();
    }

    public void addKills(int change) {
        this.setKills(Math.max(0, getKills() + change));
    }

    public void removeKills(int change) {
        this.setKills(Math.max(0, getKills() - change));
    }

    @Override
    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        this.entity.markChanged();
    }

    public void addDeaths(int change) {
        this.setDeaths(Math.max(0, getDeaths() + change));
    }

    public void removeDeaths(int change) {
        this.setDeaths(Math.max(0, getDeaths() - change));
    }

    @Override
    public int getAssists() {
        return this.assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
        this.entity.markChanged();
    }

    public void addAssists(int change) {
        this.setAssists(Math.max(0, getAssists() + change));
    }

    public void removeAssists(int change) {
        this.setAssists(Math.max(0, getAssists() - change));
    }

    @Override
    public int getLogouts() {
        return this.logouts;
    }

    public void setLogouts(int logouts) {
        this.logouts = logouts;
        this.entity.markChanged();
    }

    public void addLogouts(int change) {
        this.setLogouts(Math.max(0, getLogouts() + change));
    }

    public void removeLogouts(int change) {
        this.setLogouts(Math.max(0, getLogouts() - change));
    }

    @Override
    public float getKDR() {
        if (getDeaths() == 0) {
            return getKills();
        }

        return 1.0F * getKills() / getDeaths();
    }

}
