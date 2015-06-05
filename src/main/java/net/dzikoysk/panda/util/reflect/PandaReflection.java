package net.dzikoysk.panda.util.reflect;

import java.io.File;

import net.dzikoysk.panda.util.IOUtils;

public class PandaReflection {

	private ReflectionWorker worker;
	
	public PandaReflection(File file){
		worker = new ReflectionParser(IOUtils.getLines(file)).getResult();
	}
	
	public ReflectionWorker getWorker(){
		return this.worker;
	}
	
}
