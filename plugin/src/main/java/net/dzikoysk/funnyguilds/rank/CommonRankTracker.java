package net.dzikoysk.funnyguilds.rank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.IdentifiableEntity;
import org.jetbrains.annotations.ApiStatus;
import panda.std.Option;
import panda.std.stream.PandaStream;

abstract class CommonRankTracker<E extends IdentifiableEntity, T extends Number> implements RankTracker<E, T> {
  
    private final StatKey<T> statKey;
    
    private final Function<UUID, Option<E>> entityProvider;
    private final Predicate<E> canBeTrackedChecker;

    private final Map<UUID, T> statisticValueByEntity = new ConcurrentHashMap<>();
    private final List<UUID> entitiesInTop = new ArrayList<>();

    CommonRankTracker(
            StatKey<T> statKey,
            Function<UUID, Option<E>> entityProvider,
            Predicate<E> canBeTrackedChecker
    ) {
        this.statKey = statKey;
        this.entityProvider = entityProvider;
        this.canBeTrackedChecker = canBeTrackedChecker;
    }

    @Override
    public Collection<E> entitiesInTop() {
        List<E> entitiesInTop = PandaStream.of(this.entitiesInTop)
                .flatMap(this.entityProvider::apply)
                .toList();
        return Collections.unmodifiableList(entitiesInTop);
    }

    @Override
    public int countEntitiesInTop() {
        return this.entitiesInTop.size();
    }

    @Override
    public boolean isEntityIncludedInTop(E entity) {
        return this.canBeTrackedChecker.test(entity);
    }

    @Override
    public synchronized Option<Integer> findPositionByEntity(E entity) {
        if (!this.isEntityIncludedInTop(entity)) {
            return Option.none();
        }

        int position = this.entitiesInTop.indexOf(entity.getUUID());
        return position >= 0
                ? Option.of(position + 1)
                : Option.none();
    }

    @Override
    public synchronized Option<E> findEntityByPosition(int position) {
        return Option.flatWhen(
                position >= 1 && position <= this.entitiesInTop.size(),
                () -> Option.of(this.entitiesInTop.get(position - 1))
                        .flatMap(this.entityProvider)
        );
    }

    @Override
    public Option<T> valueOfEntity(E entity) {
        return Option.of(this.statisticValueByEntity.get(entity.getUUID()));
    }

    @ApiStatus.Internal
    synchronized void initializeEntities(Map<E, T> initialValues) {
        if (initialValues.isEmpty()) {
            return;
        }

        this.entitiesInTop.clear();
        List<UUID> entitiesInTop = PandaStream.of(initialValues.entrySet())
                .filterNot(entry -> !this.isEntityIncludedInTop(entry.getKey()))
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
                .toList();
        this.entitiesInTop.addAll(entitiesInTop);

        this.statisticValueByEntity.clear();
        Map<UUID, T> statisticValuesOfEntities = initialValues.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getUUID(),
                        Map.Entry::getValue
                ));
        this.statisticValueByEntity.putAll(statisticValuesOfEntities);
    }

    @ApiStatus.Internal
    T changeValue(
            E entity,
            UnaryOperator<T> valueChange
    ) {
        return this.statisticValueByEntity.compute(
                entity.getUUID(),
                (uuid, currentValue) -> {
                    T newValue = valueChange.apply(currentValue);
                    this.updateRanking(
                            entity,
                            newValue
                    );
                    return newValue;
                }
        );
    }

    @ApiStatus.Internal
    T setValue(
            E entity,
            T value
    ) {
        return this.statisticValueByEntity.compute(
                entity.getUUID(),
                (uuid, currentValue) -> {
                    this.updateRanking(
                            entity,
                            value
                    );
                    return value;
                }
        );
    }

    private synchronized void updateRanking(
            E entity,
            T currentValue
    ) {
        UUID uuid = entity.getUUID();
        this.entitiesInTop.remove(uuid);
        if (!this.isEntityIncludedInTop(entity)) {
            return;
        }

        int insertPosition = this.findInsertPositionBinary(
                uuid,
                currentValue
        );
        this.entitiesInTop.add(
                insertPosition,
                uuid
        );
    }

    private int findInsertPositionBinary(
            UUID uuid,
            T value
    ) {
        int left = 0;
        int right = this.entitiesInTop.size();

        while (left < right) {
            int mid = (left + right) >>> 1;
            UUID midUuid = this.entitiesInTop.get(mid);
            T midValue = this.statisticValueByEntity.get(midUuid);
            if (midValue == null) {
                return this.findInsertPositionLinear(
                        uuid,
                        value,
                        left
                );
            }

            int comparison = this.compareRankedEntities(
                    uuid,
                    value,
                    midUuid,
                    midValue
            );
            if (comparison < 0) {
                right = mid;
            }
            else {
                left = mid + 1;
            }
        }
        return left;
    }

    private int findInsertPositionLinear(
            UUID uuid,
            T value,
            int startIndex
    ) {
        int size = this.entitiesInTop.size();

        for (int i = startIndex; i < size; i++) {
            UUID currentUuid = this.entitiesInTop.get(i);
            T currentValue = this.statisticValueByEntity.get(currentUuid);

            if (currentValue == null) {
                continue;
            }

            int comparison = this.compareRankedEntities(
                    uuid,
                    value,
                    currentUuid,
                    currentValue
            );
            if (comparison < 0) {
                return i;
            }
        }

        return size;
    }

    private int compareRankedEntities(
            UUID firstUuid,
            T firstValue,
            UUID secondUuid,
            T secondValue
    ) {
        int valueComparison = this.statKey.compare(
                firstValue,
                secondValue
        );
        if (valueComparison != 0) {
            return valueComparison;
        }

        String firstEntityName = this.entityProvider.apply(firstUuid)
                .map(IdentifiableEntity::getName)
                .get();
        String secondEntityName = this.entityProvider.apply(secondUuid)
                .map(IdentifiableEntity::getName)
                .get();
        return firstEntityName.compareToIgnoreCase(secondEntityName);
    }

}
