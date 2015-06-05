package net.dzikoysk.panda.core.element;

import java.util.ArrayList;
import java.util.List;

import net.dzikoysk.panda.core.ObjectManager;

public class PandaObject {

	private final List<PandaMethod> s = new ArrayList<>();
	private final List<PandaMethod> v = new ArrayList<>();
	
	public PandaObject(){
		ObjectManager.getObjects().add(this);
	}
	
	public void registerStaticMethod(PandaMethod pm){
		s.add(pm);
	}
	
	public void registerVirtualMethod(PandaMethod pm){
		v.add(pm);
	}
	
	public List<PandaMethod> getStaticMethods(){
		return s;
	}
	
	public List<PandaMethod> getVirtualMethods(){
		return v;
	}
	
	public String getName(){
		return null;
	}
}
