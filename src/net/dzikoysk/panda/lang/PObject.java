package net.dzikoysk.panda.lang;

public class PObject {
	
	private final Object object;
	private final String type;
	
	public PObject(Object o, String type){
		this.object = o;
		this.type = type;
	}

	public String getType(){
		return this.type;
	}
	
	public Object getObject(){
		return this.object;
	}
}
