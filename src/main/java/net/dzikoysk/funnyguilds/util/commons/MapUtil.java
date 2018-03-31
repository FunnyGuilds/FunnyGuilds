package net.dzikoysk.funnyguilds.util.commons;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MapUtil {

    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map, boolean descending) {
        Stream<Entry<K, V>> stream = map.entrySet().stream();

        if (descending) {
            stream = stream.sorted(Entry.comparingByKey(Collections.reverseOrder()));
        } else {
            stream = stream.sorted(Entry.comparingByKey());
        }

        return stream.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (firstEntry, secondEntry) -> secondEntry, LinkedHashMap::new));
    }

    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
        return sortByKey(map, true);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean descending) {
        Stream<Entry<K, V>> stream = map.entrySet().stream();

        if (descending) {
            stream = stream.sorted(Entry.comparingByValue(Collections.reverseOrder()));
        } else {
            stream = stream.sorted(Entry.comparingByValue());
        }

        return stream.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (firstEntry, secondEntry) -> firstEntry, LinkedHashMap::new));
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return sortByValue(map, true);
    }

}
