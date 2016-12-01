package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.basic.util.BasicType;

public interface Basic {

    void passVariable(String... field);

    Object getVariable(String field) throws Exception;

    <T> T getVariable(String field, Class<T> clazz) throws Exception;

    BasicType getType();

    String getName();

}
