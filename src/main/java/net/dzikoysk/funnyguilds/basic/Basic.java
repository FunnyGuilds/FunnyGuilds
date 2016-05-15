package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.basic.util.BasicType;

public interface Basic {

    BasicType getType();

    String getName();

    void passVariable(String... field);

    Object getVariable(String field) throws Exception;

    <T> T getVariable(String field, Class<T> clazz) throws Exception;

}
