package net.dzikoysk.panda.util.reflect;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dzikoysk.panda.util.reflect.lang.MtdReflect;
import net.dzikoysk.panda.util.reflect.syntax.PrimitiveMethod;
import net.dzikoysk.panda.util.reflect.syntax.PrimitiveVariable;

public class ReflectionParser {

	private final ReflectionWorker worker;
	
	public ReflectionParser(String[] code){
		List<PrimitiveMethod> list = new LinkedList<>();
		for(int i = 0; i < code.length; i++){
			String line = code[i];
			if(line == null) continue;
			for(String part : line.split(";")){
				Pattern p = Pattern.compile("(.*)>>(.*)=(.*)");
				Matcher m = p.matcher(part);
				while(m.find()){
					if(m.groupCount() < 3) continue;
					PrimitiveVariable clazz = new PrimitiveVariable(m.group(1).trim());
					PrimitiveVariable field = new PrimitiveVariable(m.group(2).trim());
					PrimitiveVariable var = new PrimitiveVariable(m.group(3).trim());
					list.add(new MtdReflect(clazz, field, var));
				}
			}
		}
		PrimitiveMethod[] methods = new PrimitiveMethod[list.size()];
		methods = list.toArray(methods);
		worker = new ReflectionWorker(methods);
	}

	public ReflectionWorker getResult(){
		return worker;
	}
}
