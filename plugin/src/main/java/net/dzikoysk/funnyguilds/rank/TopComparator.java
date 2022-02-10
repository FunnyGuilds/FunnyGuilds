package net.dzikoysk.funnyguilds.rank;

import java.util.Comparator;

public interface TopComparator<T> extends Comparator<T> {

    Number getValue(T object);

    @Override
    default TopComparator<T> reversed() {
        return new ReverseTopComparator<>(this);
    }

    class ReverseTopComparator<T> implements TopComparator<T> {

        private final TopComparator<T> comparator;

        private ReverseTopComparator(TopComparator<T> comparator) {
            assert comparator != null;
            this.comparator = comparator;
        }

        @Override
        public Number getValue(T object) {
            return this.comparator.getValue(object);
        }

        @Override
        public int compare(T o1, T o2) {
            return this.comparator.compare(o2, o1);
        }
    }

}
