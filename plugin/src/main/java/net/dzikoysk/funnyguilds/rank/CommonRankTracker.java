package net.dzikoysk.funnyguilds.rank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.IdentifiableEntity;
import org.jetbrains.annotations.ApiStatus;
import panda.std.Option;

abstract class CommonRankTracker<E extends IdentifiableEntity, T extends Number> implements RankTracker<E, T> {

    private final Executor executor;

    private final StatKey<T> statKey;
    private final Function<UUID, String> nameProvider;
    private final Function<E, Boolean> canBeTrackedFunction;

    private final Queue<Runnable> topUpdateQueue = new ConcurrentLinkedQueue<>();

    private final Map<UUID, T> entitiesValues = new ConcurrentHashMap<>();
    private final List<UUID> rankedEntities = new ArrayList<>();
    private final Map<UUID, Integer> pendingChanges = new ConcurrentHashMap<>();

    CommonRankTracker(
            Executor executor,
            StatKey<T> statKey,
            RankNameProvider nameProvider,
            Function<E, Boolean> canBeTrackedFunction
    ) {
        this.executor = executor;
        this.statKey = statKey;
        this.nameProvider = nameProvider;
        this.canBeTrackedFunction = canBeTrackedFunction;
    }

    @Override
    public StatKey<T> getStatKey() {
        return this.statKey;
    }

    @Override
    public boolean canBeTracked(E entity) {
        return this.canBeTrackedFunction.apply(entity);
    }

    @Override
    public boolean hasPendingChanges(E entity) {
        return this.pendingChanges.getOrDefault(
                entity.getUUID(),
                0
        ) > 0;
    }

    @Override
    public synchronized Option<Integer> getPosition(E entity) {
        if (!this.canBeTracked(entity)) {
            return Option.none();
        }

        int position = this.rankedEntities.indexOf(entity.getUUID());
        return position >= 0
                ? Option.of(position + 1)
                : Option.none();
    }

    @Override
    public synchronized Option<UUID> getAtPosition(int position) {
        int index = position - 1;
        if (index >= 0 && index < this.rankedEntities.size()) {
            return Option.of(this.rankedEntities.get(index));
        }
        return Option.none();
    }

    @Override
    public Option<T> getValue(E entity) {
        return Option.of(this.entitiesValues.get(entity.getUUID()));
    }

    @ApiStatus.Internal
    public synchronized void initializeEntities(Map<E, T> initialValues) {
        if (initialValues.isEmpty()) {
            return;
        }

        List<UUID> sortedUuids = initialValues.entrySet()
                .stream()
                .filter(entry -> !this.canBeTracked(entry.getKey()))
                .sorted((entry1, entry2) -> {
                    E entity1 = entry1.getKey();
                    T value1 = entry1.getValue();
                    E entity2 = entry2.getKey();
                    T value2 = entry2.getValue();
                    return this.compareRankedEntities(
                            entity1.getUUID(),
                            value1,
                            entity2.getUUID(),
                            value2
                    );
                })
                .map(Map.Entry::getKey)
                .map(IdentifiableEntity::getUUID)
                .collect(Collectors.toList());

        Map<UUID, T> initialValuesByUuid = initialValues.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getUUID(),
                        Map.Entry::getValue
                ));

        this.entitiesValues.putAll(initialValuesByUuid);
        this.rankedEntities.clear();
        this.rankedEntities.addAll(sortedUuids);
    }

    @Override
    public void updateValue(
            E entity,
            T valueChange,
            Consumer<T> onComplete
    ) {
        UUID uuid = entity.getUUID();
        this.pendingChanges.merge(
                uuid,
                1,
                Integer::sum
        );

        this.topUpdateQueue.add(() -> {
            T currentValue = this.entitiesValues.merge(
                    uuid,
                    valueChange,
                    this.statKey::aggregate
            );

            this.updateRanking(
                    entity,
                    currentValue
            );

            this.pendingChanges.compute(
                    uuid,
                    (key, count) -> {
                        int newCount = count - 1;
                        return newCount > 0
                                ? newCount
                                : null;
                    }
            );

            onComplete.accept(currentValue);
        });
        this.executor.execute(() -> {
            Runnable update;
            while ((update = this.topUpdateQueue.poll()) != null) {
                update.run();
            }
        });
    }

    private synchronized void updateRanking(E entity, T currentValue) {
        UUID uuid = entity.getUUID();
        this.rankedEntities.remove(uuid);
        if (!this.canBeTracked(entity)) {
            return;
        }

        int insertPosition = this.findInsertPositionBinary(
                uuid,
                currentValue
        );
        this.rankedEntities.add(
                insertPosition,
                uuid
        );
    }

    private int findInsertPositionBinary(UUID uuid, T value) {
        int left = 0;
        int right = this.rankedEntities.size();

        while (left < right) {
            int mid = (left + right) >>> 1;
            UUID midUuid = this.rankedEntities.get(mid);
            T midValue = this.entitiesValues.get(midUuid);
            if (midValue == null) {
                return this.findInsertPositionLinear(uuid, value, left);
            }

            int comparison = this.compareRankedEntities(uuid, value, midUuid, midValue);
            if (comparison < 0) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private int findInsertPositionLinear(UUID uuid, T value, int startIndex) {
        int size = this.rankedEntities.size();

        for (int i = startIndex; i < size; i++) {
            UUID currentUuid = this.rankedEntities.get(i);
            T currentValue = this.entitiesValues.get(currentUuid);

            if (currentValue == null) {
                continue;
            }

            int comparison = this.compareRankedEntities(uuid, value, currentUuid, currentValue);
            if (comparison < 0) {
                return i;
            }
        }

        return size;
    }

    private int compareRankedEntities(
            UUID uuid1,
            T value1,
            UUID uuid2,
            T value2
    ) {
        int valueComparison = this.compareByValue(
                value1,
                value2
        );
        if (valueComparison != 0) {
            return valueComparison;
        }
        return this.compareByName(
                uuid1,
                uuid2
        );
    }

    private int compareByValue(
            T value1,
            T value2
    ) {
        return this.statKey.compare(
                value2,
                value1
        );
    }

    private int compareByName(
            UUID uuid1,
            UUID uuid2
    ) {
        String name1 = this.nameProvider.apply(uuid1);
        String name2 = this.nameProvider.apply(uuid2);
        return name1.compareTo(name2);
    }
}
