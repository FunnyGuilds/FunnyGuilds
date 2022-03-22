package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholder;
import net.dzikoysk.funnyguilds.user.User;

public class SimplePlaceholder implements Placeholder<User> {

    private final Supplier<Object> stringSupplier;

    public SimplePlaceholder(Supplier<Object> stringSupplier) {
        this.stringSupplier = stringSupplier;
    }

    @Override
    public Object getRaw(User user) {
        return this.getRaw();
    }

    public Object getRaw() {
        return this.stringSupplier.get();
    }

}
