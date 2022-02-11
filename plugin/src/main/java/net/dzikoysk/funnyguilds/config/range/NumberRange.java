package net.dzikoysk.funnyguilds.config.range;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.dzikoysk.funnyguilds.config.range.formatting.NumberRangeFormatting;
import panda.std.Option;

public final class NumberRange implements AbstractRange<Number> {

    private final Number minRange;
    private final Number maxRange;

    public NumberRange(Number minRange, Number maxRange) {
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    @Override
    public Number getMinRange() {
        return this.minRange;
    }

    @Override
    public Number getMaxRange() {
        return this.maxRange;
    }

    static <V> Option<V> inRange(Number value, Map<NumberRange, V> rangeMap) {
        for (Map.Entry<NumberRange, V> entry : rangeMap.entrySet()) {
            NumberRange range = entry.getKey();

            if (value.doubleValue() >= range.getMinRange().doubleValue() && value.doubleValue() <= range.getMaxRange().doubleValue()) {
                return Option.of(entry.getValue());
            }
        }

        return Option.none();
    }

    public static <V> String inRangeToString(Number value, Map<NumberRange, V> rangeMap) {
        return inRange(value, rangeMap)
                .map(Objects::toString)
                .orElseGet(value.toString());
    }

    public static <V> String inRangeToString(Number value, List<NumberRangeFormatting> numberFormatting) {
        return inRangeToString(value, NumberRangeFormatting.toNumberRangeMap(numberFormatting));
    }

    @Override
    public String toString() {
        return (this.minRange.doubleValue() <= Integer.MIN_VALUE ? "*" : this.minRange.toString()) + "-" + (this.maxRange.doubleValue() >= Integer.MAX_VALUE ? "*" : this.maxRange.toString());
    }

}
