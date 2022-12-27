package net.dzikoysk.funnyguilds.rank;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.dzikoysk.funnyguilds.Entity.EntityType;
import net.dzikoysk.funnyguilds.data.MutableEntity;

public abstract class Rank<T extends MutableEntity> {

    protected final T entity;
    protected Map<String, Integer> position = new HashMap<>();

    protected Rank(T entity) {
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

    /**
     * @param top the id of the top - you can use {@link DefaultTops} to get ids of default built-in tops
     *
     * @return position in which entity is for the given top, return 0 if entity is not in the top
     */
    public int getPosition(String top) {
        return this.position.getOrDefault(top.toLowerCase(Locale.ROOT), 0);
    }

    /**
     * You should not use this method since this value will be overwritten in the next top recalculation
     * It's only for internal use (or when you added your own top from your plugin)
     */
    public void setPosition(String top, int position) {
        this.position.put(top.toLowerCase(Locale.ROOT), position);
    }

    public abstract int getPoints();

    public abstract int getKills();

    public abstract int getDeaths();

    public abstract int getAssists();

    public abstract int getLogouts();

    public abstract float getKDR();

    public abstract float getKDA();

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

        Rank<?> rank = (Rank<?>) o;
        if (rank.getType() != this.getType()) {
            return false;
        }

        return this.getIdentityName().equals(rank.getIdentityName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getType(), this.getIdentityName());
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
