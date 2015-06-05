package net.dzikoysk.panda.core.element;

import net.dzikoysk.panda.core.LangElement;

public interface PandaMethod {
	
	public Object execute(Object[] variables);
	public String compile(Object[] variables);
	public boolean isStatic();
	public void setLangElement(LangElement lang);
	public LangElement getLangElement();
	
	/*
	private LangElement lang;
	
	public Object execute(Object[] variables){
		return null;
	}
	
	public String compile(Object[] variables){
		return null;
	}
	
	public boolean isStatic(){
		return false;
	}
	
	public void setLangElement(LangElement lang){
		this.lang = lang;
	}
	
	public LangElement getLangElement(){
		return this.lang;
	}
	*/
}
