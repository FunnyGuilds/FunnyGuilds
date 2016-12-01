package net.dzikoysk.funnyguilds.basic.util;

import net.dzikoysk.funnyguilds.basic.Basic;
import net.dzikoysk.funnyguilds.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

public class BasicList<T> {

    private final Collection<Basic> collection;

    public BasicList(int i) {
        this.collection = new ArrayList<>(i);
    }

    @SuppressWarnings("unchecked")
    public BasicList(Collection<? extends Basic> collection) {
        this.collection = (Collection<Basic>) collection;
    }

    public BasicList() {
        this(16);
    }

    public boolean contains(Object o) {
        return this.collection.contains(o);
    }

    public int size() {
        return this.collection.size();
    }

    public Basic[] toArray() {
        Basic[] array = new Basic[this.collection.size()];
        return this.collection.toArray(array);
    }

    public boolean add(Basic basic) {
        return !this.contains(basic) && this.collection.add(basic);
    }

    public boolean remove(Object o) {
        return this.collection.remove(o);
    }

    public boolean isEmpty() {
        return this.collection.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public Collection<T> getCollection() {
        return (Collection<T>) this.collection;
    }

    @Override
    public String toString() {
        return StringUtils.toString(BasicUtils.getNames(this.collection), false);
    }

}
