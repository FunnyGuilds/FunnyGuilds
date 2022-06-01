package net.dzikoysk.funnyguilds.shared;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MapUtil {

    private MapUtil() {
    }

    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map, boolean descending) {
        Stream<Entry<K, V>> stream = map.entrySet().stream();

        if (descending) {
            stream = stream.sorted(Entry.comparingByKey(Collections.reverseOrder()));
        }
        else {
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
        }
        else {
            stream = stream.sorted(Entry.comparingByValue());
        }

        return stream.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (firstEntry, secondEntry) -> firstEntry, LinkedHashMap::new));
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return sortByValue(map, true);
    }

    public static <K extends Comparable<? super K>, V> Entry<K, V> findTheMaximumEntryByKey(Map<K, V> map) {
        return map.entrySet().stream().max(Entry.comparingByKey()).orElse(null);
    }

    public static <K, V extends Comparable<? super V>> Entry<K, V> findTheMaximumEntryByValue(Map<K, V> map) {
        return map.entrySet().stream().max(Entry.comparingByValue()).orElse(null);
    }

    public static <K extends Comparable<? super K>, V> Entry<K, V> findTheMinimumEntryByKey(Map<K, V> map) {
        return map.entrySet().stream().min(Entry.comparingByKey()).orElse(null);
    }

    public static <K, V extends Comparable<? super V>> Entry<K, V> findTheMinimumEntryByValue(Map<K, V> map) {
        return map.entrySet().stream().min(Entry.comparingByValue()).orElse(null);
    }

}
