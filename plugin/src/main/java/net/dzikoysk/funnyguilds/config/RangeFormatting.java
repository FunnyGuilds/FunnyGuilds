package net.dzikoysk.funnyguilds.config;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RangeFormatting {

    private final NumberRange range;
    private String value;

    public RangeFormatting(NumberRange range, String value) {
        range.validate();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Range formatting requires a non-empty template");
        }
        this.range = range;
        this.value = value;
    }

    public RangeFormatting(Number min, Number max, String value) {
        this(new NumberRange(min, max), value);
    }

    public RangeFormatting(String string) {
        String[] split = string.strip().split("\\s+", 2);
        if (split.length != 2) {
            throw new IllegalArgumentException("Expected '<min>-<max> <template>', got: " + string);
        }
        this.range = NumberRange.parseStrict(split[0]);
        this.value = split[1];
    }

    public NumberRange getRange() {
        return this.range;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.range, this.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof RangeFormatting)) {
            return false;
        }

        RangeFormatting rangeFormatting = (RangeFormatting) obj;
        return this.range.equals(rangeFormatting.range) && this.value.equals(rangeFormatting.value);
    }

    @Override
    public String toString() {
        return this.range.toString() + " " + this.value;
    }

    public static Map<NumberRange, String> toRangeMap(List<RangeFormatting> formattingList) {
        Map<NumberRange, String> ranges = new LinkedHashMap<>();
        for (RangeFormatting formatting : formattingList) {
            ranges.putIfAbsent(formatting.getRange(), formatting.getValue());
        }
        return ranges;
    }

}
