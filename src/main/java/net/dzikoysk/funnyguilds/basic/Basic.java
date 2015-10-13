package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.basic.util.BasicType;

public interface Basic {

    public BasicType getType();

    public String getName();

    public void passVariable(String... field);

    public Object getVariable(String field) throws Exception;

    public <T> T getVariable(String field, Class<T> clazz) throws Exception;

}
