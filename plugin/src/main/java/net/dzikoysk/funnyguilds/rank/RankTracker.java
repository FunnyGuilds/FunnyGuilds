package net.dzikoysk.funnyguilds.rank;

import java.util.UUID;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.IdentifiableEntity;
import panda.std.Option;

public interface RankTracker<E extends IdentifiableEntity, T extends Number> {

    StatKey<T> getStatKey();

    /**
     * @param entity the entity to check
     *
     * @return whether the entity can be tracked (for e.g. they not an admin or have tracking disabled)
     */
    boolean canBeTracked(E entity);
    
    /**
     * Get the position of the entity.
     *
     * @param entity the entity to check
     *
     * @return the position (1-based index), or none if not found
     */
    Option<Integer> getPosition(E entity);

    /**
     * @param position the position (1-based index)
     *
     * @return the UUID of the entity at the position, or none if not found
     */
    Option<UUID> getAtPosition(int position);

    /**
     * Get the value associated with the entity.
     *
     * @param entity the entity to check
     *
     * @return the value associated with the entity
     */
    Option<T> getValue(E entity);

    /**
     * Update the value associated with the entity by aggregating the provided value.
     *
     * @param entity      the entity to update
     * @param updateFunction the function to aggregate the current value with the new value
     *                    
     * @return value after the update
     */
    T changeValue(
            E entity,
            UnaryOperator<T> updateFunction
    );
    
    /**
     * Set the value associated with the entity.
     *
     * @param entity the entity to update
     * @param value  the new value
     *               
     * @return value after the update
     */
    T setValue(
            E entity,
            T value
    );

}
