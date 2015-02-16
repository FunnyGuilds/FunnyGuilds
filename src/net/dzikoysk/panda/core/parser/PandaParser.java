package net.dzikoysk.panda.core.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.dzikoysk.panda.core.syntax.Method;
import net.dzikoysk.panda.core.syntax.Section;
import net.dzikoysk.panda.core.util.SectionType;
import net.dzikoysk.panda.core.util.SyntaxElement;

@SuppressWarnings("unused")
public class PandaParser {
	
	private Stack<SectionType> types = new Stack<>(); // section types
	private List<Section> sections = new ArrayList<>(); // main sections
	private Stack<Section> parents = new Stack<>(); // parent sections
	private List<SyntaxElement> elements = new ArrayList<>(); // sections, methods
	private Stack<Character> operators = new Stack<>(); // operators
	private StringBuilder node = new StringBuilder(); // node
	private boolean whitespace = false; // whitespace
	private Section current = null; // current section
	private int line = 0; // current line
	private int position = 0; // current position
	
	public PandaParser(String code){
		System.out.println(code);
		code = this.patchCode(code);
		System.out.println(code);
		for(char c : code.toCharArray()){
			position++;
			switch(c){
				case '{': { 
					this.openSection();
					break;
				} 
				case '}': {	
					this.closeSection();
					break;
				}
				case ';': { 
					this.initMethod();
					break;
				}
				case ' ': {
					this.whitespace();
					break;
				}
				case '\n':
				case '\r':{
					line++;
					break;
				}
				default: { 
					this.node.append(c);
					break;
				}
			}switch(node.toString()){
				case "event": {
					this.types.push(SectionType.EVENT_SECTION);
					break;
				}
				case "method": {
					this.types.push(SectionType.METHOD_SECTION);
					break;
				}
				case "if": {
					this.types.push(SectionType.CONDITIONS_SECTION);
					break;
				}
				case "else": {
					this.types.push(SectionType.CONDITIONS_SECTION);
					break;
				}
			}
		}
	}
	
	private String patchCode(String code){
		code = code.replace("\t", "");
		return code;
	}
	
	private void closeSection(){
		Section parent = null;
		if(!parents.empty()) parent = parents.pop();
		current.setParent(parent);
		current.addElements(elements);
		elements.clear();
		operators.pop();
		current = parent;
	}
	
	private void initMethod(){
		Method m = new MethodParser(current, node.toString()).getMethod();
		if(m != null) elements.add(m);
		node = new StringBuilder();
	}
	
	private void openSection(){
		Section section = new Section(types.pop());
		operators.push('{');
		parents.push(section);
		current = section;
	}

	private void whitespace(){
		if(	node.length() == 0 ||
			operators.isEmpty() ||
			operators.peek() == '"'
		) return;
		node.append(' ');
	}
}
