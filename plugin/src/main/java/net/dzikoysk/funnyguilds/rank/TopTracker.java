package net.dzikoysk.funnyguilds.rank;

import java.util.Collection;
import net.dzikoysk.funnyguilds.IdentifiableEntity;
import panda.std.Option;

/**
 * Tracks top-ranked entities based on a specific statistic.
 * 
 * @param <E> the type of entity being ranked
 * @param <T> the type of statistic value used for ranking
 */
public interface TopTracker<E extends IdentifiableEntity> {
    
    /**
     * @return the collection of UUIDs of entities included in the top rankings
     * @see #isEntityIncludedInTop(E)
     */
    Collection<E> entitiesInTop();

    /**
     * @return the count of entities included in the top rankings
     * @see #isEntityIncludedInTop(E)
     */
    int countEntitiesInTop();

    /**
     * @param entity the entity to check
     *
     * @return whether the entity is included in the top rankings
     */
    boolean isEntityIncludedInTop(E entity);
    
    /**
     * Get the position of the entity in the top rankings.
     *
     * @param entity the entity to check
     *
     * @return the position (1-based index), or none if not found
     */
    Option<Integer> findPositionByEntity(E entity);

    /**
     * @param position the position in range 1..N
     *
     * @return the entity at the top position, or none if not found, or out of range
     */
    Option<E> findEntityByPosition(int position);
    
}
