package net.dzikoysk.panda.core;

import java.util.ArrayList;
import java.util.List;

import net.dzikoysk.panda.core.element.PandaObject;

public class ObjectManager {

	private static ObjectManager instance;
	private static List<PandaObject> objects = new ArrayList<>();

	public ObjectManager(){
		instance = this;
	}
	
	public static List<PandaObject> getObjects(){
		return objects;
	}
	
	public static ObjectManager getInstance(){
		if(instance == null) new ObjectManager();
		return instance;
	}
}
