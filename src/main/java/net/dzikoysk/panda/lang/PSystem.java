package net.dzikoysk.panda.lang;

import net.dzikoysk.panda.Panda;
import net.dzikoysk.panda.core.LangElement;
import net.dzikoysk.panda.core.ObjectManager;
import net.dzikoysk.panda.core.element.PandaMethod;
import net.dzikoysk.panda.core.element.PandaObject;

public class PSystem extends PandaObject {
	
	static {
		PSystem system = new PSystem();
		ObjectManager.getObjects().add(system);
		Panda.registerMethod(system, new PandaMethod() {
			private LangElement lang;
			
			@Override
			public Object execute(Object[] variables){
				System.out.println(variables[0]);
				return null;
			}
			
			@Override
			public String compile(Object[] variables){
				return new String("System.out.println(" + '"' + variables[0] + '"' + ")");
			}
			
			@Override
			public boolean isStatic(){
				return true;
			}

			@Override
			public void setLangElement(LangElement lang) {
				this.lang = lang;
			}

			@Override
			public LangElement getLangElement() {
				return this.lang;
			}
		}, "print", 1);
	}
	
	@Override
	public String getName(){
		return "System";
	}
}
