package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.Entity.EntityType;
import net.dzikoysk.funnyguilds.data.MutableEntity;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

public class Rank implements Comparable<Rank> {

    private final EntityType type;
    private final MutableEntity entity;
    private final String identityName;
    private Guild guild;
    private User user;
    private int position;
    private int points;
    private int kills;
    private int deaths;
    private int assist;
    private int logouts;

    public Rank(MutableEntity entity, int rankStart) {
        this.entity = entity;
        this.type = entity.getType();
        this.identityName = entity.getName();

        if (this.type == EntityType.GUILD) {
            this.guild = (Guild) entity;
        }
        else if (this.type == EntityType.USER) {
            this.user = (User) entity;
            this.points = rankStart;
        }
    }

    public void removePoints(int value) {
        this.points -= value;

        if (this.points < 1) {
            this.points = 0;
        }

        this.entity.markChanged();
    }

    public void addPoints(int value) {
        this.points += value;
        this.entity.markChanged();
    }

    public void addKill() {
        this.kills++;
        this.entity.markChanged();
    }

    public void addDeath() {
        this.deaths++;
        this.entity.markChanged();
    }

    public void addAssist() {
        this.assist++;
        this.entity.markChanged();
    }

    public void addLogout() {
        this.logouts++;
        this.entity.markChanged();
    }

    @Override
    public int compareTo(Rank rank) {
        int result = Integer.compare(this.getPoints(), rank.getPoints());

        if (result == 0) {
            if (identityName == null) {
                return -1;
            }

            if (rank.getIdentityName() == null) {
                return 1;
            }

            result = identityName.compareTo(rank.getIdentityName());
        }

        return result;
    }

    public int getPoints() {
        if (this.type == EntityType.USER) {
            return this.points;
        }

        double points = 0;
        int size = guild.getMembers().size();

        if (size == 0) {
            return 0;
        }

        for (User user : guild.getMembers()) {
            points += user.getRank().getPoints();
        }

        double calc = points / size;

        if (calc != this.points) {
            this.points = (int) calc;
            this.entity.markChanged();
        }

        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
        this.entity.markChanged();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getKills() {
        if (this.type == EntityType.USER) {
            return this.kills;
        }
        
        int kills = 0;

        for (User user : this.guild.getMembers()) {
            kills += user.getRank().getKills();
        }
        
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        this.entity.markChanged();
    }

    public int getDeaths() {
        if (this.type == EntityType.USER) {
            return this.deaths;
        }
        
        int deaths = 0;
        for (User user : this.guild.getMembers()) {
            deaths += user.getRank().getDeaths();
        }
        
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        this.entity.markChanged();
    }

    public int getAssists() {
        if (this.type == EntityType.USER) {
            return this.assist;
        }

        int assist = 0;
        for (User user : this.guild.getMembers()) {
            assist += user.getRank().getAssists();
        }

        return assist;
    }

    public void setAssists(int assist) {
        this.assist = assist;
        this.entity.markChanged();
    }

    public int getLogouts() {
        if (this.type == EntityType.USER) {
            return this.logouts;
        }

        int logouts = 0;
        for (User user : this.guild.getMembers()) {
            logouts += user.getRank().getLogouts();
        }

        return logouts;
    }

    public void setLogouts(int logouts) {
        this.logouts = logouts;
        this.entity.markChanged();
    }

    public float getKDR() {
        if (getDeaths() == 0) {
            return getKills();
        }
    
        return 1.0F * getKills() / getDeaths();
    }

    public String getIdentityName() {
        return identityName;
    }

    public EntityType getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    public Guild getGuild() {
        return guild;
    }

    public Entity getBasic() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o.getClass() != this.getClass()) {
            return false;
        }

        final Rank rank = (Rank) o;

        if (rank.getType() != this.type) {
            return false;
        }

        return rank.getIdentityName().equals(this.identityName);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + identityName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Integer.toString(getPoints());
    }

}
