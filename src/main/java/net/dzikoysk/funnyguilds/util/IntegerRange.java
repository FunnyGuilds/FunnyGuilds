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
    
    public static <V> V inRange(int value, Map<IntegerRange, V> rangeMap) {
        for (Entry<IntegerRange, V> entry : rangeMap.entrySet()) {
            IntegerRange range = entry.getKey();
            if (value >= range.getMinRange() && value <= range.getMaxRange()) {
                return entry.getValue();
            }
        }
        
        return null;
    }
}