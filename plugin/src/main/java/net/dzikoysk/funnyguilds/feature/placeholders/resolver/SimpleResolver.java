package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import java.util.function.Supplier;

public interface SimpleResolver extends Supplier<Object> {

    Object resolve();

    @Override
    default Object get() {
        return this.resolve();
    }

}
