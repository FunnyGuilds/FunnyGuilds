package net.dzikoysk.funnyguilds.rank;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.dzikoysk.funnyguilds.Entity.EntityType;
import net.dzikoysk.funnyguilds.data.MutableEntity;
import org.jetbrains.annotations.ApiStatus;

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
     * @return position in which the player is in the top, return 0 if the player is not in the top
     */
    public int getPosition(String top) {
        return this.position.getOrDefault(top.toLowerCase(), 0);
    }

    public void setPosition(String top, int position) {
        this.position.put(top.toLowerCase(), position);
    }

    /**
     * @return player position in default top.
     * @deprecated for removal in the future, in favour of {@link Rank#getPosition(String)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "4.11.0")
    public abstract int getPosition();

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
