package net.dzikoysk.funnyguilds.rank;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
     * @return whether there are pending changes for the given entity
     */
    boolean hasPendingChanges(E entity);

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
     * Update the top value asynchronously.
     *
     * @param entity      the entity to update
     * @param valueChange the value to change (will be aggregated)
     *
     * @return a future with the updated (aggregated) value
     */
    CompletableFuture<T> updateValue(
            E entity,
            T valueChange
    );
    
    /**
     * Set the value asynchronously.
     *
     * @param entity the entity to update
     * @param value  the new value
     *
     * @return a future with the set value
     */
    CompletableFuture<T> setValue(
            E entity,
            T value
    );

}
