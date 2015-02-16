package net.dzikoysk.panda.core.syntax;

import java.util.LinkedList;
import java.util.List;

import net.dzikoysk.panda.core.util.SectionType;
import net.dzikoysk.panda.core.util.SyntaxElement;
import net.dzikoysk.panda.lang.PObject;
import net.dzikoysk.panda.util.VariableMap;

public class Section implements SyntaxElement {
	
	private final SectionType type;
	private Section parent;
	private List<SyntaxElement> elements;
	private VariableMap<String, PObject> variables;
	
	public Section(SectionType type){
		this.type = type;
		this.elements = new LinkedList<>();
	}
	
	@Override
	public void execute(){
		for(SyntaxElement le : this.elements) le.execute();
	}
	
	public void setParent(Section s){
		this.parent = s;
	}
	
	public void addElement(SyntaxElement element){
		this.elements.add(element);
	}
	
	public void addElements(List<SyntaxElement> elements){
		this.elements.addAll(elements);
	}
	
	public SectionType getType(){
		return this.type;
	}
	
	public Section getParent(){
		return this.parent;
	}
	
	public List<SyntaxElement> getLangElements(){
		return this.elements;
	}
	
	public PObject getVariable(String var){
		return this.variables.get(var);
	}
	
	@Override
	public VariableMap<String, PObject> getExecution(){
		return this.variables;
	}

}
