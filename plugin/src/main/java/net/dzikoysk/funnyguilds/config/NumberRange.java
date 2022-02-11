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
        for (Map.Entry<NumberRange, V> entry : rangeMap.entrySet()) {
            NumberRange range = entry.getKey();

            if (value.floatValue() >= range.getMinRange().floatValue() && value.floatValue() <= range.getMaxRange().floatValue() - 0.00001F) {
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
