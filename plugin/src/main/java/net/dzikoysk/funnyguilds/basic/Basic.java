package net.dzikoysk.funnyguilds.basic;

public interface Basic {

    void markChanged();

    boolean wasChanged();

    BasicType getType();

    String getName();

}
