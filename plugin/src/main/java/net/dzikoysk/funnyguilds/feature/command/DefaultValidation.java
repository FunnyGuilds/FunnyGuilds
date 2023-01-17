package net.dzikoysk.funnyguilds.feature.command;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import panda.std.Option;
import pl.peridot.yetanothermessageslibrary.message.Sendable;
import pl.peridot.yetanothermessageslibrary.replace.Replaceable;

public final class DefaultValidation {

    private DefaultValidation() {
    }

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

    public static void whenNull(Object value, Function<MessageConfiguration, Sendable> messageSupplier, Replaceable... replacements) {
        if (value == null) {
            throw new InternalValidationException(messageSupplier, replacements);
        }
    }

    public static void when(boolean flag, String message) {
        if (flag) {
            throw new ValidationException(message);
        }
    }

    public static void when(boolean flag, Function<MessageConfiguration, Sendable> messageSupplier, Replaceable... replacements) {
        if (flag) {
            throw new InternalValidationException(messageSupplier, replacements);
        }
    }

    public static <T> T when(Option<T> option, String message) {
        return option.orThrow(() -> new ValidationException(message));
    }

    public static <T> T when(Option<T> option, Function<MessageConfiguration, Sendable> messageSupplier, Replaceable... replacements) {
        return option.orThrow(() -> new InternalValidationException(messageSupplier, replacements));
    }

    public static void when(boolean flag, Supplier<Object> message) {
        if (flag) {
            throw new ValidationException(Objects.toString(message.get()));
        }
    }

}
