package net.dzikoysk.panda.core.parser;

import net.dzikoysk.panda.core.ObjectManager;
import net.dzikoysk.panda.core.element.PandaMethod;
import net.dzikoysk.panda.core.element.PandaObject;
import net.dzikoysk.panda.core.syntax.Method;
import net.dzikoysk.panda.core.syntax.Section;
import net.dzikoysk.panda.core.util.SyntaxElement;
import net.dzikoysk.panda.lang.PObject;

public class Finder {
	
	public Method findMethod(Section section, String object, String methodName, String argSection){
		for(PandaObject pp : ObjectManager.getObjects()){
			PandaMethod pm = null;
			if(object.equals(pp.getName())){
				for(PandaMethod m : pp.getStaticMethods()){
					if(!m.getLangElement().getPattern().equals(methodName)) continue;
					pm = m;
					break;
				}
			} else {
				PObject po = section.getVariable(object);
				if(po == null) break;
				if(!pp.getName().equals(po.getType())) continue;
				for(PandaMethod m : pp.getVirtualMethods()){
					if(!m.getLangElement().getPattern().equals(methodName)) continue;
					pm = m;
					break;
				}
			} 
			if(pm == null){
				System.out.println("!!! [Panda - Method Parser] Unknown method '" + methodName + "' for object @" + object);
				return null;
			}
			SyntaxElement[] les = new ArgumentParser(argSection).getElements();
			Method method = new Method(pm.getLangElement().getMethod());
			method.setArguments(les);
			section.addElement(method);
			return method;
		}
		return null;
	}

}
