package net.dzikoysk.funnyguilds.data.database.element;

@FunctionalInterface
public interface SQLSave<T> {

    Object save(T data);

}
