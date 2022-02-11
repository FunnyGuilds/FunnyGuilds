package net.dzikoysk.funnyguilds.config.range;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.dzikoysk.funnyguilds.config.range.formatting.DoubleRangeFormatting;
import panda.std.Option;

public final class DoubleRange implements AbstractRange<Double> {

    private final double minRange;
    private final double maxRange;

    public DoubleRange(double minRange, double maxRange) {
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    @Override
    public Double getMinRange() {
        return this.minRange;
    }

    @Override
    public Double getMaxRange() {
        return this.maxRange;
    }

    public static <V> Option<V> inRange(double value, Map<DoubleRange, V> rangeMap) {
        for (Map.Entry<DoubleRange, V> entry : rangeMap.entrySet()) {
            DoubleRange range = entry.getKey();

            if (value >= range.getMinRange() && value <= range.getMaxRange()) {
                return Option.of(entry.getValue());
            }
        }

        return Option.none();
    }

    public static <V> String inRangeToString(double value, Map<DoubleRange, V> rangeMap) {
        return inRange(value, rangeMap)
                .map(Objects::toString)
                .orElseGet(Double.toString(value));
    }

    public static <V> String inRangeToString(double value, List<DoubleRangeFormatting> doubleFormatting) {
        return inRangeToString(value, DoubleRangeFormatting.toIntegerRangeMap(doubleFormatting));
    }

    @Override
    public String toString() {
        return (this.minRange <= Integer.MIN_VALUE ? "*" : this.minRange) + "-" + (this.maxRange >= Integer.MAX_VALUE ? "*" : this.maxRange);
    }

}
