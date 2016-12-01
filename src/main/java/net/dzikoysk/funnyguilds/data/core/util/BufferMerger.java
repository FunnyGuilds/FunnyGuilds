package net.dzikoysk.funnyguilds.data.core.util;

import java.util.ArrayList;
import java.util.List;

public class BufferMerger<T> {

    private final List<BufferPart<T>> parts;

    public BufferMerger(int i) {
        if (i < 16) {
            i = 16;
        }
        this.parts = new ArrayList<>(i);
    }

    public void clear() {
        this.parts.clear();
    }

    public void add(T object, String... fields) {
        int i = this.parts.indexOf(object);
        if (i < 0) {
            this.parts.add(new BufferPart<>(object, fields));
        }
        else {
            BufferPart<T> part = this.parts.get(i);
            part.add(fields);
        }
    }

}



