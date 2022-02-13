package net.dzikoysk.funnyguilds.rank;

import java.util.HashMap;
import java.util.Map;
import net.dzikoysk.funnyguilds.Entity.EntityType;
import net.dzikoysk.funnyguilds.data.MutableEntity;
import panda.std.Option;

public abstract class Rank<T extends MutableEntity> {

    protected final T entity;
    protected Map<String, Integer> position = new HashMap<>();

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

    public Option<Integer> getPosition(String top) {
        return Option.of(this.position.getOrDefault(top.toLowerCase(), 0));
    }

    public void setPosition(String top, int position) {
        this.position.put(top.toLowerCase(), position);
    }

    @Deprecated
    public abstract int getPosition();

    @Deprecated
    public abstract void setPosition(int position);

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

    public static int compareName(Rank<?> o1, Rank<?> o2) {
        if (o1.getIdentityName() == null) {
            return -1;
        }

        if (o2.getIdentityName() == null) {
            return 1;
        }

        return o1.getIdentityName().compareTo(o2.getIdentityName());
    }

}
