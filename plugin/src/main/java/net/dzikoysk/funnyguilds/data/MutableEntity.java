package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.Entity;

public interface MutableEntity extends Entity {

    void markChanged();

    boolean wasChanged();

}
