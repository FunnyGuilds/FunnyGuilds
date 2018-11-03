package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class IntegerRange {

    private int minRange;
    private int maxRange;
    
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
    
    public static <V> V inRange(int value, Map<IntegerRange, V> rangeMap, String rangeType) {
        for (Entry<IntegerRange, V> entry : rangeMap.entrySet()) {
            IntegerRange range = entry.getKey();
            if (value >= range.getMinRange() && value <= range.getMaxRange()) {
                return entry.getValue();
            }
        }
        
        throw new MissingFormatException(value, rangeType);
    }

    public static Map<IntegerRange, String> parseIntegerRange(List<String> data, boolean color) {
        Map<IntegerRange, String> parsed = new HashMap<>();

        for (String s : data) {
            String[] split = s.split(" ");
            if (split.length < 2) {
                FunnyGuildsLogger.parser("\"" + s + "\" is not a valid range String!");
                continue;
            }

            String[] range = split[0].split("-");
            if (range.length < 2) {
                FunnyGuildsLogger.parser("\"" + s + "\" is not a valid integer range String!");
                continue;
            }

            int minRange = 0;
            int maxRange = 0;

            try {
                minRange = Integer.parseInt(range[0]);
            } catch (NumberFormatException e) {
                FunnyGuildsLogger.parser("\"" + range[0] + "\" of integer range String \"" + s + "\" is not a valid integer!");
                continue;
            }

            try {
                maxRange = range[1].equals("*") ? Integer.MAX_VALUE : Integer.parseInt(range[1]);
            } catch (NumberFormatException e) {
                FunnyGuildsLogger.parser("\"" + range[1] + "\" of integer range String \"" + s + "\" is not a valid integer!");
                continue;
            }

            String valueString = StringUtils.join(split, " ", 1, split.length);
            parsed.put(new IntegerRange(minRange, maxRange), color ? ChatUtils.colored(valueString) : valueString);
        }

        return parsed;
    }
    
    public static class MissingFormatException extends RuntimeException {

        private static final long serialVersionUID = -3225307636114359250L;

        public MissingFormatException(int value, String rangeType) {
            super("No format for value " + value + " and range " + rangeType + " found!");
        }

    }
    
}
