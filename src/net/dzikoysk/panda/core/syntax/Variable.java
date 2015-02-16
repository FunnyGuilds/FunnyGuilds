package net.dzikoysk.panda.core.syntax;

import net.dzikoysk.panda.core.util.SyntaxElement;
import net.dzikoysk.panda.lang.PObject;

public class Variable implements SyntaxElement {

	private final PObject object;
	
	public Variable(PObject o){
		this.object = o;
	}

	@Override
	public void execute() {
		return;
	}

	@Override
	public PObject getExecution() {
		return object;
	}
}
