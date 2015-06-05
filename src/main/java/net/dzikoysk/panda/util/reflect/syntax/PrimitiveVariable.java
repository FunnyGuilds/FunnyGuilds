package net.dzikoysk.panda.util.reflect.syntax;

public class PrimitiveVariable {

	private String var;
	
	public PrimitiveVariable(String s){
		this.var = s;
	}
	
	public Object getVariable(){
		if(var.startsWith("\"") && var.endsWith("\"")) return var.substring(1, var.length() - 2);
		if(var.equalsIgnoreCase("true")) return true;
		if(var.equalsIgnoreCase("false")) return false;
		switch(var.charAt(var.length() - 1)){
		case 'L':
		case 'l':
			return (long) Long.valueOf(getValue());
		case 'F':
		case 'f':
			return (float) Float.valueOf(getValue());
		case 'D':
		case 'd':
			return (double) Double.valueOf(getValue());
		}
		return (int) Integer.valueOf(var);
	}
	
	private String getValue(){
		return var.substring(0, var.length() - 1);
	}
	
	public String getString(){
		return this.var;
	}
	
}
