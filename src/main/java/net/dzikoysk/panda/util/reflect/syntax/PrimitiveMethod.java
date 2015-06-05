package net.dzikoysk.panda.util.reflect.syntax;

public abstract class PrimitiveMethod {

	private PrimitiveVariable[] variables;
	
	public PrimitiveMethod(PrimitiveVariable... variables){
		this.variables = variables;
	}
	
	public void execute(){
		return;
	}
	
	public PrimitiveVariable[] getVariables(){
		return this.variables;
	}

}
