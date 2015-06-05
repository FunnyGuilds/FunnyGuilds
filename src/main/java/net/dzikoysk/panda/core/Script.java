package net.dzikoysk.panda.core;

import java.util.ArrayList;
import java.util.List;

import net.dzikoysk.panda.core.syntax.Section;

public class Script {
	
	private final String name;
	private List<Section> sections;
	
	public Script(String s){
		this.name = s;
		sections = new ArrayList<>();
	}
	
	public void addSection(Section s){
		sections.add(s);
	}
	
	public String getName(){
		return this.name;
	}

}
