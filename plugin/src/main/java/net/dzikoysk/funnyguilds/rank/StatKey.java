package net.dzikoysk.funnyguilds.rank;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

/**
 * A typed ranking statistic identified by an Adventure {@link Key} and a value type - only {@link Integer}
 * and {@link Double} are supported. Keys live in a process-wide registry that is never cleared, so a key
 * obtained once stays usable for the whole plugin lifetime. Rank trackers order entities descending by
 * {@link #compare} - higher values take earlier positions.
 */
public interface StatKey<T extends Number> extends Keyed {

    /** The value class this statistic is tracked and stored as. */
    Class<T> getValueType();

    /** The default value assumed for entities without a stored value. */
    T zero();

    /** Orders two values; rank trackers sort descending by this comparison, so higher values rank first. */
    int compare(
            T value1,
            T value2
    );

    /** Returns the key registered under this identity, or registers it if new. */
    static StatKey<Integer> integerStatKey(Key key) {
        return register(new IntegerStatKey(key));
    }

    static StatKey<Integer> integerStatKey(@KeyPattern String key) {
        return integerStatKey(Key.key(
                FunnyGuilds.NAMESPACE,
                key
        ));
    }

    /** Returns the key registered under this identity, or registers it if new. */
    static StatKey<Double> doubleStatKey(Key key) {
        return register(new DoubleStatKey(key));
    }

    static StatKey<Double> doubleStatKey(@KeyPattern String key) {
        return doubleStatKey(Key.key(
                FunnyGuilds.NAMESPACE,
                key
        ));
    }

    /**
     * Finds a registered key by name: case-insensitive, bare names resolve in the {@code funnyguilds}
     * namespace. Returns empty for names that are not valid keys or are not registered; keys declared
     * through the factories are immediately resolvable and therefore usable in top placeholders.
     */
    static Optional<StatKey<?>> find(String name) {
        try {
            String normalized = name.toLowerCase(Locale.ROOT);
            Key key = normalized.indexOf(':') >= 0 ? Key.key(normalized) : Key.key(FunnyGuilds.NAMESPACE, normalized);
            return Optional.ofNullable(Registry.KEYS.get(key));
        }
        catch (InvalidKeyException invalid) {
            return Optional.empty();
        }
    }

    /**
     * Registers the key in the process-wide registry, idempotently: an equal key (same identity and value
     * type) returns the already registered instance, while the same key with a conflicting value type
     * throws {@link IllegalArgumentException}. Non {@link Integer}/{@link Double} value types are rejected.
     */
    @SuppressWarnings("unchecked")
    static <T extends Number> StatKey<T> register(StatKey<T> key) {
        if (key.getValueType() != Integer.class && key.getValueType() != Double.class) {
            throw new IllegalArgumentException("Unsupported statistic type " + key.getValueType());
        }
        // Registry state is shared across test classes; tests should use unique key names.
        StatKey<?> existing = Registry.KEYS.putIfAbsent(key.key(), key);
        if (existing != null && existing.getValueType() != key.getValueType()) {
            throw new IllegalArgumentException("Conflicting value types for statistic " + key.key());
        }
        return existing == null ? key : (StatKey<T>) existing;
    }

    final class Registry {
        private static final Map<Key, StatKey<?>> KEYS = new ConcurrentHashMap<>();

        private Registry() {
        }
    }

    abstract class CommonStatKey<T extends Number> implements StatKey<T> {

        private final Key key;

        protected CommonStatKey(Key key) {
            this.key = key;
        }

        @Override
        public @NotNull Key key() {
            return this.key;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CommonStatKey)) {
                return false;
            }
            CommonStatKey<?> that = (CommonStatKey<?>) o;
            return Objects.equals(this.key, that.key) && Objects.equals(this.getValueType(), that.getValueType());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.key);
        }

        @Override
        public String toString() {
            return "StatKey{" +
                   "key=" + this.key +
                   ", valueType=" + this.getValueType().getSimpleName() +
                   '}';
        }
    }

    final class IntegerStatKey extends CommonStatKey<Integer> {

        private IntegerStatKey(Key key) {
            super(key);
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
        public int compare(
                Integer value1,
                Integer value2
        ) {
            return Integer.compare(
                    value1,
                    value2
            );
        }
    }

    final class DoubleStatKey extends CommonStatKey<Double> {

        private DoubleStatKey(Key key) {
            super(key);
        }

        @Override
        public Class<Double> getValueType() {
            return Double.class;
        }

        @Override
        public Double zero() {
            return 0.0;
        }

        @Override
        public int compare(
                Double value1,
                Double value2
        ) {
            return Double.compare(
                    value1,
                    value2
            );
        }
    }

}
