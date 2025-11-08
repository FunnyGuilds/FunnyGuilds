package net.dzikoysk.funnyguilds.user;

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
import net.dzikoysk.funnyguilds.rank.RankNameProvider;
import net.dzikoysk.funnyguilds.rank.RankTracker;
import net.dzikoysk.funnyguilds.rank.StatKey;
import org.jetbrains.annotations.ApiStatus;
import panda.std.Option;

public final class UserRankTracker<T extends Number> implements RankTracker<T> {

    private final Executor executor;

    private final StatKey<T> statKey;
    private final Function<UUID, String> nameProvider;

    private final Queue<Runnable> topUpdateQueue = new ConcurrentLinkedQueue<>();

    private final Map<UUID, T> valuesByUuid = new ConcurrentHashMap<>();
    private final List<UUID> rankedUuids = new ArrayList<>();
    private final Map<UUID, Integer> pendingChanges = new ConcurrentHashMap<>();

    public UserRankTracker(
            Executor executor,
            StatKey<T> statKey,
            RankNameProvider nameProvider
    ) {
        this.executor = executor;
        this.statKey = statKey;
        this.nameProvider = nameProvider;
    }

    @Override
    public StatKey<T> getStatKey() {
        return this.statKey;
    }

    @Override
    public boolean canBeTracked(UUID uuid) {
        return false;
    }

    @Override
    public boolean hasPendingChanges(UUID uuid) {
        return this.pendingChanges.getOrDefault(
                uuid,
                0
        ) > 0;
    }

    @Override
    public synchronized Option<Integer> getPosition(UUID uuid) {
         if (!this.canBeTracked(uuid)) {
            return Option.none();
        }
        
        int position = this.rankedUuids.indexOf(uuid);
        return position >= 0
                ? Option.of(position + 1)
                : Option.none();
    }

    @Override
    public synchronized Option<UUID> getAtPosition(int position) {
        int index = position - 1;
        if (index >= 0 && index < this.rankedUuids.size()) {
            return Option.of(this.rankedUuids.get(index));
        }
        return Option.none();
    }

    @Override
    public Option<T> getValue(UUID uuid) {
        return Option.of(this.valuesByUuid.get(uuid));
    }

    @ApiStatus.Internal
    public void initializeUsers(Map<UUID, T> initialValues) {
         if (initialValues.isEmpty()) {
            return;
        }
    
        List<UUID> sortedUuids = new ArrayList<>();
        
        for (Map.Entry<UUID, T> entry : initialValues.entrySet()) {
            UUID uuid = entry.getKey();
            if (this.canBeTracked(uuid)) {
                sortedUuids.add(uuid);
            }
        }
    
        sortedUuids.sort((uuid1, uuid2) -> {
            T value1 = initialValues.get(uuid1);
            T value2 = initialValues.get(uuid2);
            return this.compareRankedUsers(uuid1, value1, uuid2, value2);
        });
    
        synchronized (this) {
            this.valuesByUuid.putAll(initialValues);
            this.rankedUuids.clear();
            this.rankedUuids.addAll(sortedUuids);
        }
    }

    /**
     * Update the top value for a user asynchronously.
     *
     * @param uuid        the UUID of the user
     * @param valueChange the value to change (will be aggregated)
     * @param onComplete  the callback to execute when the update is complete
     */
    @Override
    public void updateValue(
            UUID uuid,
            T valueChange,
            Consumer<T> onComplete
    ) {
        this.pendingChanges.merge(
                uuid,
                1,
                Integer::sum
        );

        this.topUpdateQueue.add(() -> {
            T currentValue = this.valuesByUuid.merge(
                    uuid,
                    valueChange,
                    this.statKey::aggregate
            );

            this.updateRanking(
                    uuid,
                    currentValue
            );

            // Decrement pending counter
            this.pendingChanges.compute(
                    uuid,
                    (key, count) -> {
                        int newCount = count - 1;
                        return newCount > 0
                                ? newCount
                                : null; // Remove if 0
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

    /**
     * Updates the ranking position of a user based on their current value.
     * Uses binary search to find the correct insertion position.
     * This method must be called while holding the monitor lock.
     *
     * @param uuid         the user's UUID
     * @param currentValue the user's current stat value
     */
    private synchronized void updateRanking(UUID uuid, T currentValue) {
        this.rankedUuids.remove(uuid);
        if (!this.canBeTracked(uuid)) {
            return;
        }
        
        int insertPosition = this.findInsertPositionBinary(
                uuid,
                currentValue
        );
        this.rankedUuids.add(
                insertPosition,
                uuid
        );
    }

    /**
     * Finds the correct insertion position for a user in the ranked list using binary search.
     * This method assumes the ranked list is already sorted according to the comparison logic.
     * Falls back to linear search if it encounters a null value in the map.
     *
     * @param uuid  the UUID of the user to insert
     * @param value the stat value of the user to insert
     *
     * @return the index where the user should be inserted to maintain sort order
     */
    private int findInsertPositionBinary(UUID uuid, T value) {
        int left = 0;
        int right = this.rankedUuids.size();
    
        while (left < right) {
            int mid = (left + right) >>> 1;
            UUID midUuid = this.rankedUuids.get(mid);
            T midValue = this.valuesByUuid.get(midUuid);
            if (midValue == null) {
                return this.findInsertPositionLinear(uuid, value, left);
            }
    
            int comparison = this.compareRankedUsers(uuid, value, midUuid, midValue);
            if (comparison < 0) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    /**
     * Finds the correct insertion position for a user using linear search from a starting index.
     * This is a fallback method used when binary search encounters null values in the value map,
     * or when the ranked list is small enough that linear search is more efficient.
     * Skips over entries with null values.
     *
     * @param uuid       the UUID of the user to insert
     * @param value      the stat value of the user to insert
     * @param startIndex the index to start searching from
     *
     * @return the index where the user should be inserted to maintain sort order
     */
    private int findInsertPositionLinear(UUID uuid, T value, int startIndex) {
        int size = this.rankedUuids.size();
    
        for (int i = startIndex; i < size; i++) {
            UUID currentUuid = this.rankedUuids.get(i);
            T currentValue = this.valuesByUuid.get(currentUuid);
            
            if (currentValue == null) {
                continue;
            }
    
            int comparison = this.compareRankedUsers(uuid, value, currentUuid, currentValue);
            if (comparison < 0) {
                return i;
            }
        }
    
        return size;
    }

    private int compareRankedUsers(
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
