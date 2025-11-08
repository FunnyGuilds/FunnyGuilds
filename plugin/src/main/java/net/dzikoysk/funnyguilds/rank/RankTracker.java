package net.dzikoysk.funnyguilds.rank;

import java.util.UUID;
import java.util.function.Consumer;
import panda.std.Option;

public interface RankTracker<T extends Number> {
    
    StatKey<T> getStatKey();

    /**
     * @param uuid the UUID of the entity
     * @return whether the entity can be tracked (for e.g. they not an admin or have tracking disabled)
     */
    boolean canBeTracked(UUID uuid);
    
    /**
     * @return whether there are pending changes for the given UUID
     */
    boolean hasPendingChanges(UUID uuid);
    
    /**
     * Get the position, by entity UUID.
     *
     * @param uuid the UUID of the entity
     *
     * @return the position (1-based index), or none if not found
     */
    Option<Integer> getPosition(UUID uuid);
    
    /**
     * @param position the position (1-based index)
     *
     * @return the UUID of the entity at the position, or none if not found
     */
    Option<UUID> getAtPosition(int position);
    
    /**
     * Get the value associated with the UUID.
     * @param uuid the UUID of the entity to update
     * @return the value associated with the UUID, or none if not found
     */
    Option<T> getValue(UUID uuid);
    
    /**
     * Update the top value asynchronously.
     *
     * @param uuid        the UUID of the entity to update
     * @param valueChange the value to change (will be aggregated)
     * @param onComplete  the callback to execute when the update is complete
     */
    void updateValue(
            UUID uuid,
            T valueChange,
            Consumer<T> onComplete
    );
}
