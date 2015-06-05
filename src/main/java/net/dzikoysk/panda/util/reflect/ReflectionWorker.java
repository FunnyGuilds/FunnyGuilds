package net.dzikoysk.panda.util.reflect;

import net.dzikoysk.panda.util.reflect.syntax.PrimitiveMethod;

public class ReflectionWorker implements Runnable {

	private final PrimitiveMethod[] methods;
	private boolean wait;
	
	public ReflectionWorker(PrimitiveMethod... methods){
		this.methods = methods;
	}
	
	@Override
	public void run() {
		wait = false;
		for(int i = 0; i < methods.length; i++){
			PrimitiveMethod method = methods[i];
			methods[i] = null;
			method.execute();
			if(wait) return;
		}
	}
	
	public void disable(){
		wait = true;
	}

}
