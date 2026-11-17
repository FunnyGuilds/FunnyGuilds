package net.dzikoysk.funnyguilds.rank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.IdentifiableEntity;
import panda.std.Pair;

abstract class CommonRankTracker<E extends IdentifiableEntity, T extends Number> implements RankTracker<E, T> {

    private final StatKey<T> statKey;
    private final Function<UUID, Optional<E>> entityProvider;
    private final Predicate<E> canBeTrackedChecker;
    private final Map<UUID, Entry<T>> entries = new HashMap<>();
    private final List<Entry<T>> ranking = new ArrayList<>();
    private boolean rankingInitialized;

    CommonRankTracker(StatKey<T> statKey, Function<UUID, Optional<E>> entityProvider, Predicate<E> canBeTrackedChecker) {
        this.statKey = statKey;
        this.entityProvider = entityProvider;
        this.canBeTrackedChecker = canBeTrackedChecker;
    }

    @Override
    public synchronized Collection<E> entitiesInTop() {
        this.initializeRanking();
        return this.ranking.stream()
                .map(entry -> this.entityProvider.apply(entry.uuid))
                .filter(Optional::isPresent)
                .map(value -> value.orElseThrow(IllegalStateException::new))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    public synchronized int countEntitiesInTop() {
        this.initializeRanking();
        return this.ranking.size();
    }

    @Override
    public synchronized boolean isEntityIncludedInTop(E entity) {
        Entry<T> entry = this.entries.get(entity.getUUID());
        return entry != null && entry.ranked;
    }

    @Override
    public synchronized Optional<Integer> findPositionByEntity(E entity) {
        this.initializeRanking();
        Entry<T> entry = this.entries.get(entity.getUUID());
        if (entry == null || !entry.ranked) {
            return Optional.empty();
        }
        int index = Collections.binarySearch(this.ranking, entry, this::compare);
        return index < 0 ? Optional.empty() : Optional.of(index + 1);
    }

    @Override
    public synchronized Optional<E> findEntityByPosition(int position) {
        this.initializeRanking();
        return position < 1 || position > this.ranking.size()
                ? Optional.empty()
                : this.entityProvider.apply(this.ranking.get(position - 1).uuid);
    }

    @Override
    public synchronized Optional<T> valueOfEntity(E entity) {
        return Optional.ofNullable(this.entries.get(entity.getUUID())).map(entry -> entry.value);
    }

    @Override
    public synchronized Optional<Pair<E, T>> findEntryByPosition(int position) {
        return this.findEntityByPosition(position).flatMap(entity -> this.valueOfEntity(entity)
                .map(value -> Pair.of(entity, value)));
    }

    synchronized void initializeEntities(Map<E, T> initialValues) {
        this.entries.clear();
        this.ranking.clear();
        initialValues.forEach((entity, value) -> {
            Entry<T> entry = this.entry(entity, value);
            this.entries.put(entry.uuid, entry);
            if (this.rankingInitialized && entry.ranked) {
                this.ranking.add(entry);
            }
        });
        if (this.rankingInitialized) {
            this.ranking.sort(this::compare);
        }
    }

    synchronized T changeValue(E entity, UnaryOperator<T> valueChange) {
        T current = this.valueOfEntity(entity).orElseGet(this.statKey::zero);
        return this.setValue(entity, valueChange.apply(current));
    }

    synchronized T setValue(E entity, T value) {
        Objects.requireNonNull(value, "statistic value");
        Entry<T> updated = this.entry(entity, value);
        Entry<T> previous = this.entries.get(updated.uuid);
        if (previous != null && Objects.equals(previous.value, value)
                && previous.name.equals(updated.name) && previous.ranked == updated.ranked) {
            return value;
        }
        this.remove(entity);
        this.entries.put(updated.uuid, updated);
        if (this.rankingInitialized && updated.ranked) {
            int index = Collections.binarySearch(this.ranking, updated, this::compare);
            this.ranking.add(-index - 1, updated);
        }
        return value;
    }

    synchronized void remove(E entity) {
        Entry<T> previous = this.entries.remove(entity.getUUID());
        if (this.rankingInitialized && previous != null && previous.ranked) {
            int index = Collections.binarySearch(this.ranking, previous, this::compare);
            if (index >= 0) {
                this.ranking.remove(index);
            }
        }
    }

    private void initializeRanking() {
        if (this.rankingInitialized) {
            return;
        }
        this.entries.values().stream().filter(entry -> entry.ranked).forEach(this.ranking::add);
        this.ranking.sort(this::compare);
        this.rankingInitialized = true;
    }

    private Entry<T> entry(E entity, T value) {
        return new Entry<>(entity.getUUID(), entity.getName(), value, this.canBeTrackedChecker.test(entity));
    }

    private int compare(Entry<T> first, Entry<T> second) {
        int valueComparison = this.statKey.compare(second.value, first.value);
        if (valueComparison != 0) {
            return valueComparison;
        }
        int nameComparison = first.name.compareToIgnoreCase(second.name);
        return nameComparison != 0 ? nameComparison : first.uuid.compareTo(second.uuid);
    }

    // Comparisons use snapshots: names and values may change before an old entry is removed.
    private static final class Entry<T extends Number> {
        private final UUID uuid;
        private final String name;
        private final T value;
        private final boolean ranked;

        private Entry(UUID uuid, String name, T value, boolean ranked) {
            this.uuid = uuid;
            this.name = name;
            this.value = value;
            this.ranked = ranked;
        }
    }
}
