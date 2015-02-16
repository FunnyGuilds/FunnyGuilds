package net.dzikoysk.panda.util.reflect.lang;

import java.lang.reflect.Field;

import net.dzikoysk.panda.util.reflect.syntax.PrimitiveMethod;
import net.dzikoysk.panda.util.reflect.syntax.PrimitiveVariable;

public class MtdReflect  extends PrimitiveMethod {

	public MtdReflect(PrimitiveVariable clazz, PrimitiveVariable field, PrimitiveVariable var){
		super(clazz, field, var);
	}
	
	@Override
	public void execute(){
		String clazzName = (String) this.getVariables()[0].getString();
		String fieldName = (String) this.getVariables()[1].getString();
		Object var = this.getVariables()[2].getVariable();
		try {
			Class<?> clazz = Class.forName(clazzName);
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(null, var);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
