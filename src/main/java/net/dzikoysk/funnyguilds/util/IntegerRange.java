package net.dzikoysk.funnyguilds.util;

import java.util.Collection;

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
    
    public static IntegerRange inRange(int value, Collection<IntegerRange> ranges) {
        for (IntegerRange range : ranges) {
            if (value >= range.getMinRange() && value <= range.getMaxRange()) {
                return range;
            }
        }
        
        return null;
    }
}