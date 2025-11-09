package net.dzikoysk.funnyguilds.rank;

import java.util.Objects;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface StatKey<T extends Number> extends Keyed {

    Class<T> getValueType();

    int compare(
            T value1,
            T value2
    );

    static StatKey<Integer> integerStatKey(Key key) {
        return new IntegerStatKey(key);
    }

    static StatKey<Integer> integerStatKey(@KeyPattern String key) {
        return new IntegerStatKey(Key.key(
                FunnyGuilds.NAMESPACE,
                key
        ));
    }

    static StatKey<Double> doubleStatKey(Key key) {
        return new DoubleStatKey(key);
    }

    static StatKey<Double> doubleStatKey(@KeyPattern String key) {
        return new DoubleStatKey(Key.key(
                FunnyGuilds.NAMESPACE,
                key
        ));
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
            return Objects.equals(this.key, that.key);
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
