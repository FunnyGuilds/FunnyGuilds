package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.IdentifiableEntity;
import panda.std.Option;

/**
 * Track statistics for entities.
 * 
 * @param <E> the type of entity being ranked
 * @param <T> the type of statistic value used for ranking
 */
public interface StatTracker<E extends IdentifiableEntity, T extends Number> {

    /**
     * Get the statistic value associated with the entity.
     *
     * @param entity the entity to check
     *
     * @return the value associated with the entity, or none if not found
     */
    Option<T> valueOfEntity(E entity);

}
