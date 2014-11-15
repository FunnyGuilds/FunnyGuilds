package net.dzikoysk.funnyguilds.util.configuration;

import java.util.Map;
import java.util.Stack;

public class ConfigurationParser {
	
	private final String[] code;
	private Map<String, ConfigurationObject> result;
	
	public ConfigurationParser(String[] code){
		this.code = code;
		this.run();
	}
	
	public void run(){
		//Stack<String> keys = new Stack<>();
		//Stack<String> elements = new Stack<>();
		Stack<Character> operators = new Stack<>();
		
		StringBuilder chars = new StringBuilder();
		
		for(String line : code){
			if(line == null || line.isEmpty()) continue;
			boolean whitespace = true;
			for(char c : line.toCharArray()){
				switch(c){ 
				case ' ':
					if(whitespace) continue;
				case ':':
					operators.push(c);
				case '-': 
					operators.push(c);
				default:
					chars.append(c);
				}
				whitespace = false;
			}
			String string = chars.toString();
			if(string.isEmpty()) continue;
			
			/*
			 * key: value
			 * key: value
			 * 
			 * list:
			 * - value
			 * - value
			 * 
			 */
		}
	}

	public Map<String, ConfigurationObject> getResult(){
		return this.result;
	}
}
