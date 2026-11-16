package net.dzikoysk.funnyguilds.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;

public final class NumberRange {

    private static final Pattern RANGE_PATTERN = Pattern.compile("(-?[0-9.*]+)-(-?[0-9.*]+)");
    private static final Pattern STRICT_RANGE_PATTERN = Pattern.compile("(-?(?:\\*|[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+))-(-?(?:\\*|[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+))");

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

    public static NumberRange parseStrict(String text) {
        Matcher matcher = STRICT_RANGE_PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid number range: " + text);
        }
        NumberRange range = new NumberRange(
                parseBoundary(matcher.group(1), Integer.MIN_VALUE),
                parseBoundary(matcher.group(2), Integer.MAX_VALUE)
        );
        range.validate();
        return range;
    }

    private static Number parseBoundary(String text, int wildcard) {
        if (text.equals("*") || text.equals("-*")) {
            return wildcard;
        }
        if (text.contains(".")) {
            return Double.parseDouble(text);
        }
        return Integer.parseInt(text);
    }

    public void validate() {
        double min = this.minRange.doubleValue();
        double max = this.maxRange.doubleValue();
        if (!Double.isFinite(min) || !Double.isFinite(max) || min > max
                || (min == max && !(this.minRange instanceof Integer && this.maxRange instanceof Integer))) {
            throw new IllegalArgumentException("Invalid or empty number range: " + this.minRange + "-" + this.maxRange);
        }
    }

    public boolean contains(Number value) {
        double number = value.doubleValue();
        return number >= this.minRange.doubleValue()
                && (this.minRange instanceof Integer && this.maxRange instanceof Integer
                    ? number <= this.maxRange.doubleValue()
                    : number < this.maxRange.doubleValue());
    }

    public static <V> Optional<V> inRange(Number value, Map<NumberRange, V> rangeMap) {
        return rangeMap.entrySet().stream()
                .filter(entry -> entry.getKey().contains(value))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public static <V> String inRangeToString(Number value, Map<NumberRange, V> rangeMap, boolean color) {
        return inRange(value, rangeMap)
                .map(Objects::toString)
                .map(string -> color ? ChatUtils.colored(string) : string)
                .orElse(value.toString());
    }

    public static <V> String inRangeToString(Number value, Map<NumberRange, V> rangeMap) {
        return inRangeToString(value, rangeMap, false);
    }

    public static String inRangeToString(Number value, List<RangeFormatting> numberFormatting, boolean color) {
        return inRangeToString(value, RangeFormatting.toRangeMap(numberFormatting), color);
    }

    public static String inRangeToString(Number value, List<RangeFormatting> numberFormatting) {
        return inRangeToString(value, numberFormatting, false);
    }

    public static Map<NumberRange, String> parseIntegerRange(List<String> rangeEntries, boolean color) {
        return rangeEntries.stream()
                .map(RangeFormatting::new)
                .collect(Collectors.toUnmodifiableMap(RangeFormatting::getRange, (formatting) -> color
                        ? ChatUtils.colored(formatting.getValue())
                        : formatting.getValue()
                ));
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
            FunnyGuilds.getPluginLogger().error("Failed to parse a range boundary: " + numberString, exception);
            return borderValue;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.minRange, this.maxRange);
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
        return this.minRange.equals(range.minRange) && this.maxRange.equals(range.maxRange);
    }

    @Override
    public String toString() {
        String min = this.minRange.doubleValue() <= Integer.MIN_VALUE ? "*" : this.minRange.toString();
        String max = this.maxRange.doubleValue() >= Integer.MAX_VALUE ? "*" : this.maxRange.toString();

        return min + "-" + max;
    }

}
