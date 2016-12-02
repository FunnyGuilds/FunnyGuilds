package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.basic.util.BasicType;

public interface Basic {

    void changes();

    boolean changed();

    BasicType getType();

    String getName();

}
