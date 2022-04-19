package net.dzikoysk.funnyguilds.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import panda.std.Option;
import panda.std.stream.PandaStream;

public final class NumberRange {

    private static final Pattern RANGE_PATTERN = Pattern.compile("(-?[0-9.*]+)-(-?[0-9.*]+)");

    private final Number minRange;
    private final Number maxRange;

    public NumberRange(Number minRange, Number maxRange) {
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    public NumberRange(String string) {
        Matcher matcher = RANGE_PATTERN.matcher(string);

        Number min = Integer.MIN_VALUE;
        Number max = Integer.MAX_VALUE;
        if (matcher.matches()) {
            min = parseNumber(matcher.group(1), Integer.MIN_VALUE);
            max = parseNumber(matcher.group(2), Integer.MAX_VALUE);
        }

        this.minRange = min;
        this.maxRange = max;
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

    public static <V> String inRangeToString(Number value, Map<NumberRange, V> rangeMap, boolean color) {
        return inRange(value, rangeMap)
                .map(Objects::toString)
                .map(string -> color ? ChatUtils.colored(string) : string)
                .orElseGet(value.toString());
    }

    public static <V> String inRangeToString(Number value, Map<NumberRange, V> rangeMap) {
        return inRangeToString(value, rangeMap, false);
    }

    public static <V> String inRangeToString(Number value, List<RangeFormatting> numberFormatting, boolean color) {
        return inRangeToString(value, RangeFormatting.toRangeMap(numberFormatting), color);
    }

    public static <V> String inRangeToString(Number value, List<RangeFormatting> numberFormatting) {
        return inRangeToString(value, numberFormatting, false);
    }

    public static Map<NumberRange, String> parseIntegerRange(List<String> rangeEntries, boolean color) {
        return PandaStream.of(rangeEntries)
                .map(RangeFormatting::new)
                .toMap(RangeFormatting::getRange, (formatting) -> color ? ChatUtils.colored(formatting.getValue()) : formatting.getValue());
    }

    private static Number parseNumber(String numberString, Number borderValue) {
        try {
            if (numberString.contains("*")) {
                return borderValue;
            }
            else {
                if (numberString.contains(".")) {
                    return Double.parseDouble(numberString);
                }
                else {
                    return Integer.parseInt(numberString);
                }
            }
        }
        catch (NumberFormatException exception) {
            exception.printStackTrace();
            return borderValue;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(minRange, maxRange);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NumberRange)) {
            return false;
        }
        NumberRange range = (NumberRange) obj;

        return this.minRange.equals(range.minRange) &&
                this.maxRange.equals(range.maxRange);
    }

    @Override
    public String toString() {
        return (this.minRange.doubleValue() <= Integer.MIN_VALUE ? "*" : this.minRange.toString()) + "-" + (this.maxRange.doubleValue() >= Integer.MAX_VALUE ? "*" : this.maxRange.toString());
    }

}
