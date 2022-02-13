package net.dzikoysk.funnyguilds.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import panda.std.Option;
import panda.std.stream.PandaStream;

public final class NumberRange {

    private final Number minRange;
    private final Number maxRange;

    public NumberRange(Number minRange, Number maxRange) {
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    public Number getMinRange() {
        return this.minRange;
    }

    public Number getMaxRange() {
        return this.maxRange;
    }

    public static <V> Option<V> inRange(Number value, Map<NumberRange, V> rangeMap) {
        return PandaStream.of(rangeMap.entrySet())
                .find((entry) -> {
                    NumberRange range = entry.getKey();

                    Number minRange = range.getMinRange();
                    Number maxRange = range.getMaxRange();
                    if (minRange instanceof Integer && maxRange instanceof Integer) {
                        return value.floatValue() >= minRange.intValue() && value.floatValue() <= maxRange.intValue();
                    }
                    return value.floatValue() >= minRange.floatValue() && value.floatValue() < maxRange.floatValue();
                })
                .map(Map.Entry::getValue);
    }

    public static <V> String inRangeToString(Number value, Map<NumberRange, V> rangeMap) {
        return inRange(value, rangeMap)
                .map(Objects::toString)
                .orElseGet(value.toString());
    }

    public static <V> String inRangeToString(Number value, List<RangeFormatting> numberFormatting) {
        return inRangeToString(value, RangeFormatting.toRangeMap(numberFormatting));
    }

    public static Map<NumberRange, String> parseIntegerRange(List<String> rangeEntries, boolean color) {
        return PandaStream.of(rangeEntries)
                .map(RangeFormatting::new)
                .toMap(RangeFormatting::getRange, (formatting) -> color ? ChatUtils.colored(formatting.getValue()) : formatting.getValue());
    }

    @Override
    public String toString() {
        return (this.minRange.doubleValue() <= Integer.MIN_VALUE ? "*" : this.minRange.toString()) + "-" + (this.maxRange.doubleValue() >= Integer.MAX_VALUE ? "*" : this.maxRange.toString());
    }

}
