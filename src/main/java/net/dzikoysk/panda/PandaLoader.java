package net.dzikoysk.panda;

import java.io.File;

import net.dzikoysk.panda.core.parser.PandaParser;
import net.dzikoysk.panda.util.IOUtils;

public class PandaLoader {

	public static void load(File file){
		if(file.isDirectory()) return;
		load(IOUtils.getContent(file));
	}
	
	public static void load(String code){
		new PandaParser(code);
	}
	
	public static void loadLang() {
		String[] classes = new String[] { 
			"PObject",
			"PString",
			"PSystem"
		};
		for(String s : classes){
			try {
				Class.forName("net.dzikoysk.panda.lang." + s);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
