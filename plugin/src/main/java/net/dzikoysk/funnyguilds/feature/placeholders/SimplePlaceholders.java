package net.dzikoysk.funnyguilds.feature.placeholders;

public class SimplePlaceholders<T> extends Placeholders<T, SimplePlaceholders<T>> {

    @Override
    public SimplePlaceholders<T> create() {
        return new SimplePlaceholders<>();
    }

}
