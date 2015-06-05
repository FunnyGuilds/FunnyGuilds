package net.dzikoysk.panda.core.parser;
import java.util.Stack;

import net.dzikoysk.panda.core.ObjectManager;
import net.dzikoysk.panda.core.element.PandaMethod;
import net.dzikoysk.panda.core.element.PandaObject;
import net.dzikoysk.panda.core.syntax.Method;
import net.dzikoysk.panda.core.syntax.Section;
import net.dzikoysk.panda.core.util.SyntaxElement;
import net.dzikoysk.panda.lang.PObject;

public class MethodParser {

	private Section section;
	private Method method;
	private String object;
	private String methodName;
	private String argSection;
	
	private Stack<Character> operators = new Stack<>();
	private StringBuilder node = new StringBuilder();
	
	protected MethodParser(Section section, String recognize){
		if(section == null) return;
		this.section = section;
		for(char c : recognize.toCharArray()){
			switch(c){
				case '.':
					createObject();
					break;
				case '(':
					openArgumentSection();
					break;
				case ')':
					closeArgumentSection();
					break;
				case ';':
					break;
				case '\r':
					break;
				default:
					node.append(c);
					break;
			}
		}
		for(PandaObject pp : ObjectManager.getObjects()){
			PandaMethod pm = null;
			System.out.println(object + ":" + pp.getName() + ":" + object.equals(pp.getName()));
			if(object.equals(pp.getName())){
				System.out.println("Class: " + object);
				for(PandaMethod m : pp.getStaticMethods()){
					if(!m.getLangElement().getPattern().equals(methodName)) continue;
					pm = m;
					break;
				}
			} else {
				PObject po = section.getVariable(object);
				if(po == null) break;
				if(!pp.getName().equals(po.getType())) continue;
				for(PandaMethod m : pp.getVirtualMethods()){
					if(!m.getLangElement().getPattern().equals(methodName)) continue;
					pm = m;
					break;
				}
			} 
			if(pm == null){
				System.out.println("!!! [Panda - Method Parser] Unknown method '" + methodName + "' for object @" + object);
				return;
			}
			SyntaxElement[] les = new ArgumentParser(argSection).getElements();
			method = new Method(pm.getLangElement().getMethod());
			method.setArguments(les);
			section.addElement(method);
			System.out.println("break");
			break;
		}
		System.out.println("end");
	}
	
	private void createObject(){
		object = node.toString();
		node.setLength(0);
		object.length();
		System.out.println(object);
	}
	
	private void openArgumentSection(){
		methodName = node.toString();
		operators.push('(');
		node.setLength(0);
	}
	
	private void closeArgumentSection(){
		argSection = node.toString();
		operators.peek();
		node.setLength(0);
	}
	
	protected Section getSection(){
		return section;
	}
	
	protected Method getMethod(){
		return method;
	}
}
