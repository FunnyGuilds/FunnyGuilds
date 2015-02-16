package net.dzikoysk.panda.util.configuration.util;

import java.util.Map.Entry;

public class PandaEntry<K, V> implements Entry<K, V> {
    
	private K key;
    private V value;

    public PandaEntry() {
    }
    
    public PandaEntry(K key, V value) {
    	this.key = key;
    	this.value = value;
    }
    
    public K setKey(K key) {
        K old = this.key;
        this.key = key;
        return old;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
    
    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }
    
   
}