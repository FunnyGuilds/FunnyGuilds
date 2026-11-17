package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.IdentifiableEntity;
import java.util.Optional;
import panda.std.Pair;

/**
 * A live ranking of entities for one statistic: tracked values ({@link StatTracker}) and one-based
 * positions ({@link TopTracker}), maintained incrementally as values and eligibility change.
 */
public interface RankTracker<E extends IdentifiableEntity, T extends Number> extends StatTracker<E, T>, TopTracker<E> {

    /**
     * Returns the entity and its value at the given one-based position as a single consistent snapshot,
     * or empty when the position is out of range or the entity no longer resolves.
     */
    Optional<Pair<E, T>> findEntryByPosition(int position);
}
