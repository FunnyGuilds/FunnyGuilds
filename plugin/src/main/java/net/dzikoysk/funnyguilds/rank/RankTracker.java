package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.IdentifiableEntity;

public interface RankTracker<E extends IdentifiableEntity, T extends Number> extends StatTracker<E, T>, TopTracker<E> {
}
