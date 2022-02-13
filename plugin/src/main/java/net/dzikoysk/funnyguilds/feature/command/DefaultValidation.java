package net.dzikoysk.funnyguilds.feature.command;

import java.util.Objects;
import java.util.function.Supplier;
import net.dzikoysk.funnycommands.resources.ValidationException;
import panda.std.Option;

public final class DefaultValidation {

    private DefaultValidation() {}

    public static void whenNull(Object value, Supplier<Object> message) {
        if (value == null) {
            throw new ValidationException(Objects.toString(message.get()));
        }
    }

    public static void whenNull(Object value, String message) {
        if (value == null) {
            throw new ValidationException(message);
        }
    }

    public static void when(boolean flag, String message) {
        if (flag) {
            throw new ValidationException(message);
        }
    }

    public static <T> T when(Option<T> option, String message) {
        if (option.isEmpty()) {
            throw new ValidationException(message);
        }
        return option.get();
    }

    public static void when(boolean flag, Supplier<Object> message) {
        if (flag) {
            throw new ValidationException(Objects.toString(message.get()));
        }
    }

}
