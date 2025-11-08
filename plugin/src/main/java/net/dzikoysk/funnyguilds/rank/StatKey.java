package net.dzikoysk.funnyguilds.rank;

import net.kyori.adventure.key.Keyed;

public interface StatKey<T extends Number> extends Keyed {

    Class<T> getValueType();    
    
    int compare(T value1, T value2);
    
    T aggregate(T value1, T value2);
}
