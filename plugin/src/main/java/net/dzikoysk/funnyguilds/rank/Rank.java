package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.Entity.EntityType;
import net.dzikoysk.funnyguilds.data.MutableEntity;

public abstract class Rank implements Comparable<Rank> {

    protected final MutableEntity entity;

    protected int position;

    public Rank(MutableEntity entity) {
        this.entity = entity;
    }

    public MutableEntity getEntity() {
        return this.entity;
    }

    public Entity getBasic() {
        return this.entity;
    }

    public EntityType getType() {
        return this.entity.getType();
    }

    public String getIdentityName() {
        return this.entity.getName();
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public abstract int getPoints();

    public abstract int getKills();

    public abstract int getDeaths();

    public abstract int getAssists();

    public abstract int getLogouts();

    public abstract float getKDR();

    @Override
    public int compareTo(Rank rank) {
        int result = Integer.compare(this.getPoints(), rank.getPoints());

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

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o.getClass() != this.getClass()) {
            return false;
        }

        final Rank rank = (Rank) o;

        if (rank.getType() != this.getType()) {
            return false;
        }

        return rank.getIdentityName().equals(this.getIdentityName());
    }

    @Override
    public int hashCode() {
        int result = getType().hashCode();
        result = 31 * result + getIdentityName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Integer.toString(getPoints());
    }

}
