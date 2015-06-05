package net.dzikoysk.panda.core;

public class ScriptInfo {
	
	private final String name;
	private String version;
	private String author;
	
	public ScriptInfo(String s){
		this.name = s;
	}
	
	public ScriptInfo author(String s){
		this.author = s;
		return this;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getVersion(){
		return this.version;
	}
	
	public String getAuthor(){
		return this.author;
	}

}
