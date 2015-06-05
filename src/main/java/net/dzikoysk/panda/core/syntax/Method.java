package net.dzikoysk.panda.core.syntax;

import net.dzikoysk.panda.core.element.PandaMethod;
import net.dzikoysk.panda.core.util.SyntaxElement;

public class Method implements SyntaxElement {
	
	private final PandaMethod method;
	private SyntaxElement[] arguments;
	private Object res;
	
	public Method(PandaMethod method){
		this.method = method;
	}

	public void setArguments(SyntaxElement[] les){
		this.arguments = les;
	}

	@Override
	public void execute() {
		Object[] variables = new Object[arguments.length];
		for(int i = 0; i < arguments.length; i++){
			arguments[i].execute();
			variables[i] = arguments[i].getExecution();
		}
		this.res = method.execute(variables);
	}
	
	@Override
	public Object getExecution(){
		return this.res;
	}
}
