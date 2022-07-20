package net.dzikoysk.funnyguilds.user;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Map that allows only for modifications of existing keys.
 */
public class FixedSizeMap<K, V> implements Map<K, V> {

    private final Map<K, V> map;

    public FixedSizeMap(Map<K, V> map) {
        this.map = new LinkedHashMap<>(map);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.map.get(key);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        if (this.containsKey(key)) {
            throw new UnsupportedOperationException("FixedSizeMap only allows modifying values of existing keys");
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("FixedSizeMap only allows modifying values of existing keys");
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> map) {
        if (this.map.keySet().containsAll(map.keySet())) {
            throw new UnsupportedOperationException("FixedSizeMap only allows modifying values of existing keys");
        }

        this.map.putAll(map);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("FixedSizeMap only allows modifying values of existing keys");
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return this.map.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return this.map.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.map.entrySet();
    }

}
