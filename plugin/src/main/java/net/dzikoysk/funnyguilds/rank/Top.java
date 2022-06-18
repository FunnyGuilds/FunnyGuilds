package net.dzikoysk.funnyguilds.rank;

import com.google.common.collect.Iterables;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import panda.std.Option;

public class Top<T> {

    private final TopComparator<T> comparator;
    private final BiFunction<String, TopComparator<T>, NavigableSet<T>> recalculateFunction;
    private NavigableSet<T> values;

    public Top(TopComparator<T> comparator, BiFunction<String, TopComparator<T>, NavigableSet<T>> recalculateFunction) {
        this.comparator = comparator;
        this.recalculateFunction = recalculateFunction;
        this.values = new TreeSet<>(comparator);
    }

    public TopComparator<T> getComparator() {
        return this.comparator;
    }

    public Option<T> get(int place) {
        return Option.when(place > 0 && place <= this.values.size(), () -> Iterables.get(this.values, place - 1));
    }

    public int count() {
        return this.values.size();
    }

    public void recalculate(String id) {
        this.values = this.recalculateFunction.apply(id, this.comparator);
    }

}
