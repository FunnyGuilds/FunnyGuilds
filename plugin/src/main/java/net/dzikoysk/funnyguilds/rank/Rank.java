package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.Entity.EntityType;
import net.dzikoysk.funnyguilds.data.MutableEntity;

public abstract class Rank<T extends MutableEntity> {

    protected final T entity;
    protected int position;

    public Rank(T entity) {
        this.entity = entity;
    }

    public MutableEntity getEntity() {
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
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (o.getClass() != this.getClass()) {
            return false;
        }

        final Rank rank = (Rank) o;

        if (rank.getType() != this.getType()) {
            return false;
        }

        return this.getIdentityName().equals(rank.getIdentityName());
    }

    @Override
    public int hashCode() {
        int result = this.getType().hashCode();
        result = 31 * result + this.getIdentityName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Integer.toString(this.getPoints());
    }

}
