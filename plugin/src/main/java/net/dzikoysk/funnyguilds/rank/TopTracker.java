package net.dzikoysk.funnyguilds.rank;

import java.util.Collection;
import java.util.Optional;
import net.dzikoysk.funnyguilds.IdentifiableEntity;

/**
 * Tracks top-ranked entities based on a specific statistic.
 *
 * @param <E> the type of entity being ranked
 * @param <T> the type of statistic value used for ranking
 */
public interface TopTracker<E extends IdentifiableEntity> {

    /**
     * @return the entities included in the top rankings, ordered from the first to the last position;
     * entities that no longer resolve (e.g. removed outside of their manager) are filtered out,
     * so this collection may be shorter than what {@link #countEntitiesInTop()} reports
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
    Optional<Integer> findPositionByEntity(E entity);

    /**
     * @param position the position in range 1..N
     *
     * @return the entity at the top position, or none if not found, or out of range
     */
    Optional<E> findEntityByPosition(int position);

}
