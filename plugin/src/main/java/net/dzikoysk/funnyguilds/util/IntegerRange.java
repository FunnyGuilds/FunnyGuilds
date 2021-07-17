package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import panda.std.Option;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public final class IntegerRange {

    private final int minRange;
    private final int maxRange;
    
    public IntegerRange(int minRange, int maxRange) {
        this.minRange = minRange;
        this.maxRange = maxRange;
    }
    
    public int getMinRange() {
        return this.minRange;
    }
    
    public int getMaxRange() {
        return this.maxRange;
    }

    public static <V> Option<V> inRange(int value, Map<IntegerRange, V> rangeMap) {
        for (Entry<IntegerRange, V> entry : rangeMap.entrySet()) {
            IntegerRange range = entry.getKey();

            if (value >= range.getMinRange() && value <= range.getMaxRange()) {
                return Option.of(entry.getValue());
            }
        }
        
        return Option.none();
    }

    public static <V> String inRangeToString(int value, Map<IntegerRange, V> rangeMap) {
        return inRange(value, rangeMap)
                .map(Objects::toString)
                .orElseGet(Integer.toString(value));
    }

    public static Map<IntegerRange, String> parseIntegerRange(List<String> rangeEntries, boolean color) {
        Map<IntegerRange, String> parsed = new HashMap<>();

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
            } catch (NumberFormatException numberFormatException) {
                FunnyGuilds.getPluginLogger().parser("\"" + minRangeValue + "\" of integer range String \"" + rangeEntry + "\" is not a valid integer!");
                continue;
            }

            try {
                maxRange = maxRangeValue.equals("*") ? Integer.MAX_VALUE : Integer.parseInt(maxRangeValue);
            } catch (NumberFormatException numberFormatException) {
                FunnyGuilds.getPluginLogger().parser("\"" + maxRangeValue + "\" of integer range String \"" + rangeEntry + "\" is not a valid integer!");
                continue;
            }

            String valueString = StringUtils.join(rangeParts, " ", 1, rangeParts.length);

            if (rangeEntry.endsWith(" ")) {
                valueString += " ";
            }
            
            parsed.put(new IntegerRange(minRange, maxRange), color ? ChatUtils.colored(valueString) : valueString);
        }

        return parsed;
    }
    
    public static class MissingFormatException extends RuntimeException {

        public MissingFormatException(int value, String rangeType) {
            super("No format for value " + value + " and range " + rangeType + " found!");
        }

    }
    
}
