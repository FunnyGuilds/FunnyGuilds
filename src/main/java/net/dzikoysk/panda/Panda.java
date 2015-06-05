package net.dzikoysk.panda;

import net.dzikoysk.panda.core.LangElement;
import net.dzikoysk.panda.core.element.PandaMethod;
import net.dzikoysk.panda.core.element.PandaObject;

public class Panda {
	
	public static void main(String[] args) throws Exception {
		PandaLoader.loadLang();
	}
	
	public static void registerMethod(PandaObject object, PandaMethod method, String pattern, int args){
		try {
			new LangElement(object, method, pattern, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getDirectory() {
		return Panda.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	}
}
