package net.dzikoysk.funnyguilds.util;

import java.util.Map;
import java.util.Map.Entry;

public class IntegerRange {

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
    
    private static class MissingFormatException extends RuntimeException {
        
        private static final long serialVersionUID = -3686306422518954346L;

        public MissingFormatException(int value, String rangeType) {
            super("No format for value " + value + " and range " + rangeType + " found!");
        }
    }
    
}
