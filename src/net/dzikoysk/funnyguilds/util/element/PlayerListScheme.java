package net.dzikoysk.funnyguilds.util.element;

import java.util.ArrayList;
import java.util.List;

public class PlayerListScheme {
	
	private static String[] scheme = new String[60];
	private static List<Integer> edit = new ArrayList<>();
	
	public PlayerListScheme(String[] ss){
		scheme = ss;
		this.update();
	}
	
	private void update(){
		edit.clear();
		for(int i = 0; i < 60; i++)
			if(scheme[i] != null && scheme[i].contains("{") && scheme[i].contains("}"))
				edit.add(i);
	}
	
	public static String[] getScheme(){
		return scheme.clone();
	}
	
	public static List<Integer> getEdit(){
		return edit;
	}
}
