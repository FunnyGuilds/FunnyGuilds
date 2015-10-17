package net.dzikoysk.funnyguilds.data.core.util;

import java.util.ArrayList;
import java.util.List;

public class BufforMerger<T> {

    private final List<BufforPart<T>> parts;

    public BufforMerger(int i) {
        if (i < 16)
            i = 16;
        this.parts = new ArrayList<>(i);
    }

    public void add(T object, String... fields) {
        int i = this.parts.indexOf(object);
        if (i < 0)
            this.parts.add(new BufforPart<T>(object, fields));
        else {
            BufforPart<T> part = this.parts.get(i);
            part.add(fields);
        }
    }

    public void clear() {
        this.parts.clear();
    }

}



