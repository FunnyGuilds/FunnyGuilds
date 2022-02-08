package net.dzikoysk.funnyguilds.rank;

import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Function;
import panda.std.Option;

public class Top<T> {

    private final Comparator<T> comparator;
    private final Function<Comparator<T>, NavigableSet<T>> recalculateFunction;

    private NavigableSet<T> values;

    public Top(Comparator<T> comparator, Function<Comparator<T>, NavigableSet<T>> recalculateFunction) {
        this.comparator = comparator;
        this.recalculateFunction = recalculateFunction;

        this.values = new TreeSet<>(Collections.reverseOrder(comparator));
    }

    public Comparator<T> getComparator() {
        return comparator;
    }

    public Option<T> get(int place) {
        if (place - 1 < this.values.size()) {
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
