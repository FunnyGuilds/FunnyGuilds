package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.basic.util.BasicType;

public interface Basic {
	
	public BasicType getType();
	public String getName();
	
	public void changes();
	public boolean changed();
	
}
