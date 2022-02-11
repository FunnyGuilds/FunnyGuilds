package net.dzikoysk.funnyguilds.config.range.formatting;

import net.dzikoysk.funnyguilds.config.range.AbstractRange;

public abstract class RangeFormatting<T extends AbstractRange<?>> {

    protected final T range;
    protected final String value;

    public RangeFormatting(T range, String value) {
        this.range = range;
        this.value = value;
    }

    public T getRange() {
        return range;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.range.toString() + " " + this.value;
    }

}
