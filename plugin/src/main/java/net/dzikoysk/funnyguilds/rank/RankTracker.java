package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.IdentifiableEntity;
import java.util.Optional;
import panda.std.Pair;

public interface RankTracker<E extends IdentifiableEntity, T extends Number> extends StatTracker<E, T>, TopTracker<E> {

    Optional<Pair<E, T>> findEntryByPosition(int position);
}
