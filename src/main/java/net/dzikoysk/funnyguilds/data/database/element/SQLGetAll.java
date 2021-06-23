package net.dzikoysk.funnyguilds.data.database.element;

import net.dzikoysk.funnyguilds.basic.Basic;

import java.util.Collection;

@FunctionalInterface
public interface SQLGetAll<T extends Basic> {

    Collection<T> getAll();

}
