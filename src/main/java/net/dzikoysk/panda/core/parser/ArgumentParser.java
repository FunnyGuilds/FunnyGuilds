package net.dzikoysk.panda.core.parser;

import java.util.Stack;

import net.dzikoysk.panda.core.syntax.Method;
import net.dzikoysk.panda.core.syntax.Section;
import net.dzikoysk.panda.core.util.SyntaxElement;

public class ArgumentParser {

	private Stack<Character> operators = new Stack<>();
	private StringBuilder node = new StringBuilder();
	private Section section;
	private String object;
	private boolean string;
	private boolean args;
	private String argSection;
	
	public ArgumentParser(String recognize) {
		if(recognize == null) return;
		for(char c : recognize.toCharArray()){
			switch(c){
				case '"':
					string = !string;
					break;
				case '.':
					dot();
					break;
				case '(':
					open();
					break;
				case ')':
					close();
					break;
				case ',':
					comma();
					break;
				default:
					node.append(c);
					break;
			}
		}
	}
	
	private void dot(){
		if(catcher('.')) return;
		String s = node.toString();
		node.setLength(0);
		if(object == null){
			object = s;
		} else {
			Method method = new Finder().findMethod(section, object, s, argSection);
			method.execute();
		}
	}

	private void comma(){
		if(catcher(',')) return;
		if(!brackets(',')) return;
		operators.push(',');
	}
	
	private void open(){
		if(catcher('(')) return;
		args = true;
	}
	
	private void close(){
		if(catcher(')')) return;
		if(!brackets(',')) return;
		args = false;
	}
	
	private boolean catcher(char c){
		if(!string) return false;
		node.append(c);
		return true;
	}
	
	private boolean brackets(char with){
		if(!args) return false;
		Stack<Character> check = new Stack<>();
		int size = operators.size();
		for(int i = 0; i < size; i++){
			char c = operators.get(i);
			switch(c){
				case '(':
					check.push(c);
					break;
				case ')':
					for(int x = 0; x < check.size(); x++){
						if(check.get(x) == '('){
							check.pop();
							break;
						}
						check.pop();
					}
				default:
					break;
			}
		}
		boolean b = check.isEmpty();
		if(!b){
			node.append(with);
		}
		return b;
	}
	
	protected SyntaxElement[] getElements(){
		return null;
	}
}
