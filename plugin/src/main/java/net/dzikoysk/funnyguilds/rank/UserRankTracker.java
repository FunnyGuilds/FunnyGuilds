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
import net.dzikoysk.funnyguilds.user.User;
import org.jetbrains.annotations.ApiStatus;
import panda.std.Option;

final class UserRankTracker<T extends Number> implements RankTracker<User, T> {

    private final Executor executor;

    private final StatKey<T> statKey;
    private final Function<UUID, String> nameProvider;
    private final Function<User, Boolean> canBeTrackedFunction;

    private final Queue<Runnable> topUpdateQueue = new ConcurrentLinkedQueue<>();

    private final Map<UUID, T> valuesByUuid = new ConcurrentHashMap<>();
    private final List<UUID> rankedUuids = new ArrayList<>();
    private final Map<UUID, Integer> pendingChanges = new ConcurrentHashMap<>();

    UserRankTracker(
            Executor executor,
            StatKey<T> statKey,
            RankNameProvider nameProvider,
            Function<User, Boolean> canBeTrackedFunction
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
    public boolean canBeTracked(User entity) {
        return this.canBeTrackedFunction.apply(entity);
    }

    @Override
    public boolean hasPendingChanges(User entity) {
        return this.pendingChanges.getOrDefault(
                entity.getUUID(),
                0
        ) > 0;
    }

    @Override
    public synchronized Option<Integer> getPosition(User entity) {
         if (!this.canBeTracked(entity)) {
            return Option.none();
        }
        
        int position = this.rankedUuids.indexOf(entity.getUUID());
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
    public Option<T> getValue(User entity) {
        return Option.of(this.valuesByUuid.get(entity.getUUID()));
    }

    @ApiStatus.Internal
    public synchronized void initializeUsers(Map<User, T> initialValues) {
         if (initialValues.isEmpty()) {
            return;
        }

        List<UUID> sortedUsersUuids = initialValues.entrySet()
                .stream()
                .filter(entry -> !this.canBeTracked(entry.getKey()))
                .sorted((entry1, entry2) -> {
                    User user1 = entry1.getKey();
                    T value1 = entry1.getValue();
                    User user2 = entry2.getKey();
                    T value2 = entry2.getValue();
                    return this.compareRankedUsers(
                            user1.getUUID(),
                            value1,
                            user2.getUUID(),
                            value2
                    );
                })
                .map(Map.Entry::getKey)
                .map(User::getUUID)
                .collect(Collectors.toList());
         
         Map<UUID, T> initialValuesByUuid = initialValues.entrySet()
                 .stream()
                 .collect(Collectors.toMap(
                         entry -> entry.getKey().getUUID(),
                         Map.Entry::getValue
                 ));
         
            this.valuesByUuid.putAll(initialValuesByUuid);
            this.rankedUuids.clear();
            this.rankedUuids.addAll(sortedUsersUuids);
    }
    
    @Override
    public void updateValue(
            User user,
            T valueChange,
            Consumer<T> onComplete
    ) {
        UUID uuid = user.getUUID();
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
                    user,
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
    
    private synchronized void updateRanking(User user, T currentValue) {
        UUID uuid = user.getUUID();
        this.rankedUuids.remove(uuid);
        if (!this.canBeTracked(user)) {
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
