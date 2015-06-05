package net.dzikoysk.panda.core;

import net.dzikoysk.panda.core.element.PandaMethod;
import net.dzikoysk.panda.core.element.PandaObject;

public class LangElement {
	
	private PandaObject object;
	private PandaMethod method;
	private String pattern;
	private int arguments;
	
	public LangElement(PandaObject object, PandaMethod method, String pattern, int args) throws Exception {
		this.object = object;
		this.method = method;
		this.pattern = pattern;
		this.arguments = args;
		if(method.isStatic()) object.registerStaticMethod(method);
		else object.registerVirtualMethod(method);
		method.setLangElement(this);
	}
	
	public PandaObject getObject(){
		return this.object;
	}
	
	public PandaMethod getMethod(){
		return method;
	}
	
	public int getArguments(){
		return this.arguments;
	}
	
	public String getPattern(){
		return this.pattern;
	}
}
