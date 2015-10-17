package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.data.Settings;

public class Rank implements Comparable<Rank> {

    private BasicType type;
    private Basic basic;
    private String idns;
    private Guild guild;
    private User user;
    private int points;
    private int kills;
    private int deaths;

    public Rank(Basic basic) {
        this.basic = basic;
        this.type = basic.getType();
        this.idns = basic.getName();
        if (this.type == BasicType.GUILD)
            this.guild = (Guild) basic;
        else if (this.type == BasicType.USER) {
            this.user = (User) basic;
            this.points = Settings.getInstance().rankStart;
        }
    }

    public void removePoints(int i) {
        this.points -= i;
        if (this.points < 1)
            this.points = 0;
        this.basic.passVariable("rank");
    }

    public void addPoints(int i) {
        this.points += i;
        this.basic.passVariable("rank");
    }

    public void addKill() {
        this.kills += 1;
        this.basic.passVariable("rank");
    }

    public void addDeath() {
        this.deaths += 1;
        this.basic.passVariable("rank");
    }

    public int getPoints() {
        if (this.type == BasicType.USER)
            return this.points;
        else {
            double points = 0;
            int size = guild.getMembers().size();
            if (size == 0)
                return 0;
            for (User user : guild.getMembers())
                points += user.getRank().getPoints();
            double calc = points / size;
            if (calc != this.points) {
                this.points = (int) calc;
                this.basic.passVariable("rank");
            }
            return this.points;
        }
    }

    public void setPoints(int i) {
        this.points = i;
        this.basic.passVariable("rank");
    }

    public int getKills() {
        return this.kills;
    }

    public void setKills(int i) {
        this.kills = i;
        this.basic.passVariable("rank");
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(int i) {
        this.deaths = i;
        this.basic.passVariable("rank");
    }

    public String getIDNS() {
        return idns;
    }

    public BasicType getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    public Guild getGuild() {
        return guild;
    }

    public Basic getBasic() {
        return basic;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;
        Rank rank = (Rank) o;
        if (rank.getType() != type)
            return false;
        if (!rank.getIDNS().equalsIgnoreCase(idns))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(getPoints());
    }

    @Override
    public int compareTo(Rank rank) {
        int i = Integer.compare(rank.getPoints(), getPoints());
        if (i == 0) {
            if (idns == null)
                return -1;
            if (rank.getIDNS() == null)
                return 1;
            i = idns.compareTo(rank.getIDNS());
        }
        return i;
    }

}
