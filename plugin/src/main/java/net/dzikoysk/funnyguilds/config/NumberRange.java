package net.dzikoysk.funnyguilds.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import panda.std.Option;

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
        for (Entry<NumberRange, V> entry : rangeMap.entrySet()) {
            NumberRange range = entry.getKey();

            if (value.floatValue() >= range.getMinRange().floatValue() && value.floatValue() <= range.getMaxRange().floatValue()) {
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

    public static <V> String inRangeToString(Number value, List<NumberFormatting> numberFormatting) {
        return inRangeToString(value, NumberFormatting.toNumberRangeMap(numberFormatting));
    }

    public static Map<NumberRange, String> parseIntegerRange(List<String> rangeEntries, boolean color) {
        Map<NumberRange, String> parsed = new HashMap<>();

        for (String rangeEntry : rangeEntries) {
            String[] rangeParts = rangeEntry.split(" ", 2);

            if (rangeParts.length != 2) {
                FunnyGuilds.getPluginLogger().parser("\"" + rangeEntry + "\" is not a valid range String!");
                continue;
            }

            String rangeValue = rangeParts[0].trim();

            int splitOperator = rangeValue.startsWith("-")
                    ? rangeValue.indexOf('-', 1)
                    : rangeValue.indexOf('-');

            if (splitOperator == -1) {
                FunnyGuilds.getPluginLogger().parser("\"" + rangeEntry + "\" is not a valid integer range String!");
                continue;
            }

            String minRangeValue = rangeValue.substring(0, splitOperator).trim();
            String maxRangeValue = rangeValue.substring(splitOperator + 1).trim();

            int minRange;
            int maxRange;

            try {
                minRange = minRangeValue.equals("-*") ? Integer.MIN_VALUE : Integer.parseInt(minRangeValue);
            }
            catch (NumberFormatException numberFormatException) {
                FunnyGuilds.getPluginLogger().parser("\"" + minRangeValue + "\" of integer range String \"" + rangeEntry + "\" is not a valid integer!");
                continue;
            }

            try {
                maxRange = maxRangeValue.equals("*") ? Integer.MAX_VALUE : Integer.parseInt(maxRangeValue);
            }
            catch (NumberFormatException numberFormatException) {
                FunnyGuilds.getPluginLogger().parser("\"" + maxRangeValue + "\" of integer range String \"" + rangeEntry + "\" is not a valid integer!");
                continue;
            }

            String valueString = StringUtils.join(rangeParts, " ", 1, rangeParts.length);

            if (rangeEntry.endsWith(" ")) {
                valueString += " ";
            }

            parsed.put(new NumberRange(minRange, maxRange), color ? ChatUtils.colored(valueString) : valueString);
        }

        return parsed;
    }

    @Override
    public String toString() {
        return (this.minRange.floatValue() <= Integer.MIN_VALUE ? "*" : this.minRange.toString()) + "-" + (this.maxRange.floatValue() >= Integer.MAX_VALUE ? "*" : this.maxRange.toString());
    }

    public static class MissingFormatException extends RuntimeException {

        public MissingFormatException(int value, String rangeType) {
            super("No format for value " + value + " and range " + rangeType + " found!");
        }

    }

}
