package net.dzikoysk.funnyguilds.feature.placeholders;

public interface PlaceholdersService<T> {

    String format(String text, T data);

}
