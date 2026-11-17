package net.dzikoysk.funnyguilds.rank;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.dzikoysk.funnyguilds.Entity.EntityType;
import net.dzikoysk.funnyguilds.IdentifiableEntity;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonRankTrackerTest {

    private final Map<UUID, TestEntity> entities = new HashMap<>();
    private final Set<UUID> excluded = new HashSet<>();

    private <T extends Number> CommonRankTracker<TestEntity, T> tracker(StatKey<T> key) {
        return new CommonRankTracker<TestEntity, T>(key,
                uuid -> Optional.ofNullable(this.entities.get(uuid)),
                entity -> !this.excluded.contains(entity.getUUID())) { };
    }

    private TestEntity entity(long id, String name) {
        TestEntity entity = new TestEntity(new UUID(0, id), name);
        this.entities.put(entity.getUUID(), entity);
        return entity;
    }

    @Test
    void doesNotSortUnrequestedTopsOrResortAllEntriesAfterAnUpdate() {
        AtomicInteger comparisons = new AtomicInteger();
        StatKey<Integer> key = new StatKey<Integer>() {
            @Override
            public Key key() {
                return Key.key("test", "points");
            }

            @Override
            public Class<Integer> getValueType() {
                return Integer.class;
            }

            @Override
            public Integer zero() {
                return 0;
            }

            @Override
            public int compare(Integer first, Integer second) {
                comparisons.incrementAndGet();
                return Integer.compare(first, second);
            }
        };
        CommonRankTracker<TestEntity, Integer> tracker = this.tracker(key);
        TestEntity changed = null;
        for (int i = 0; i < 4096; i++) {
            TestEntity entity = this.entity(i, "Player" + i);
            tracker.setValue(entity, i);
            if (i == 0) {
                changed = entity;
            }
        }
        assertEquals(0, comparisons.get(), "collecting persisted stats must not sort on every loaded user");
        assertEquals(4096, tracker.countEntitiesInTop());
        comparisons.set(0);
        tracker.setValue(changed, 5000);
        assertEquals(1, tracker.findPositionByEntity(changed).orElse(0));
        assertTrue(comparisons.get() < 100, "an update must use binary searches instead of sorting 4096 entries");
        comparisons.set(0);
        tracker.setValue(changed, 5000);
        assertEquals(0, comparisons.get(), "unchanged entries must not be repositioned");
    }

    @Test
    void preservesEqualNamesAndRemovesTheSnapshotBeforeRenaming() {
        CommonRankTracker<TestEntity, Integer> tracker = this.tracker(StatKey.integerStatKey("points"));
        TestEntity first = this.entity(1, "Same");
        TestEntity second = this.entity(2, "same");
        TestEntity third = this.entity(3, "Zed");
        tracker.setValue(first, 10);
        tracker.setValue(second, 10);
        tracker.setValue(third, 10);
        assertEquals(3, tracker.countEntitiesInTop());
        assertEquals(first, tracker.findEntityByPosition(1).orElse(null));
        third.name = "Aaron";
        tracker.setValue(third, 10);
        assertEquals(third, tracker.findEntityByPosition(1).orElse(null));
        assertEquals(3, tracker.countEntitiesInTop());
        tracker.remove(third);
        assertEquals(first, tracker.findEntityByPosition(1).orElse(null));
        assertFalse(tracker.valueOfEntity(third).isPresent());
        assertThrows(UnsupportedOperationException.class, () -> tracker.entitiesInTop().clear());
    }

    @Test
    void preservesValuesForExcludedEntitiesAndCanClearAnInitializedRanking() {
        CommonRankTracker<TestEntity, Integer> tracker = this.tracker(StatKey.integerStatKey("points"));
        TestEntity entity = this.entity(1, "Player");
        tracker.setValue(entity, 20);
        assertEquals(1, tracker.countEntitiesInTop());
        this.excluded.add(entity.getUUID());
        tracker.setValue(entity, 20);
        assertEquals(0, tracker.countEntitiesInTop());
        assertEquals(20, tracker.valueOfEntity(entity).orElse(0));
        assertFalse(tracker.findPositionByEntity(entity).isPresent());
        this.excluded.clear();
        tracker.setValue(entity, 20);
        assertEquals(1, tracker.countEntitiesInTop());
        tracker.initializeEntities(new HashMap<>());
        assertEquals(0, tracker.countEntitiesInTop());
        assertFalse(tracker.valueOfEntity(entity).isPresent());
        assertFalse(tracker.findEntityByPosition(-1).isPresent());
    }

    private static final class TestEntity implements IdentifiableEntity {
        private final UUID uuid;
        private String name;

        private TestEntity(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public EntityType getType() {
            return EntityType.USER;
        }
    }
}
