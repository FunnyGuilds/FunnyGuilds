package net.dzikoysk.panda.util;

import java.util.HashMap;
import java.util.Map;

import net.dzikoysk.panda.lang.PObject;

public class VariableMap<K, V> {
	
	private final VariableMap<String, PObject> parent;
	private final Map<String, PObject> local;
	
	public VariableMap(){
		this.local = new HashMap<String, PObject>();
		this.parent = null;
	}
	
	public VariableMap(VariableMap<String, PObject> parent){
		this.local = new HashMap<String, PObject>();
		this.parent = parent;
	}
	
	public void put(String key, PObject o){
		if(this.parent.parentContainsKey(key)) this.parent.put(key, o);
		else this.local.put(key, o);
	}
	
	public PObject get(String name){
		PObject o;
		o = local.get(name);
		if(o == null && parent != null) o = parent.get(name);
		return o;
	}
	
	public boolean containsKey(String name){
		if(local.containsKey(name)) return true;
		if(parent != null) if(parent.containsKey(name)) return true;
		return false;
	}
	
	public boolean parentContainsKey(String name){
		if(parent != null) if(parent.containsKey(name)) return true;
		return false;
	}
	
	public VariableMap<String, PObject> getParent(){
		return this.parent;
	}
	
	public Map<String, PObject> getLocal(){
		return this.local;
	}
}
