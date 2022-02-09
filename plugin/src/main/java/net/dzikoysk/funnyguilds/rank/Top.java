package net.dzikoysk.funnyguilds.rank;

import com.google.common.collect.Iterables;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Function;
import panda.std.Option;

public class Top<T> {

    private final TopComparator<T> comparator;
    private final Function<TopComparator<T>, NavigableSet<T>> recalculateFunction;

    private NavigableSet<T> values;

    public Top(TopComparator<T> comparator, Function<TopComparator<T>, NavigableSet<T>> recalculateFunction) {
        this.comparator = comparator;
        this.recalculateFunction = recalculateFunction;

        this.values = new TreeSet<>(comparator);
    }

    public TopComparator<T> getComparator() {
        return comparator;
    }

    public Option<T> get(int place) {
        if (place > 0 && place - 1 < this.values.size()) {
            return Option.of(Iterables.get(this.values, place - 1));
        }
        return Option.none();
    }

    public int count() {
        return this.values.size();
    }

    public void recalculate() {
        this.values = this.recalculateFunction.apply(this.comparator);
    }

}
