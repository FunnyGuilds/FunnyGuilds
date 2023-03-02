package net.dzikoysk.funnyguilds.feature.command;

import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import panda.std.Option;
import dev.peri.yetanothermessageslibrary.message.Sendable;
import dev.peri.yetanothermessageslibrary.replace.Replaceable;

public final class DefaultValidation {

    private DefaultValidation() {
    }

    public static void whenNull(Object value, Function<MessageConfiguration, Sendable> messageSupplier, Replaceable... replacements) {
        if (value == null) {
            throw new InternalValidationException(messageSupplier, replacements);
        }
    }

    public static void when(boolean flag, Function<MessageConfiguration, Sendable> messageSupplier, Replaceable... replacements) {
        if (flag) {
            throw new InternalValidationException(messageSupplier, replacements);
        }
    }

    public static <T> T when(Option<T> option, Function<MessageConfiguration, Sendable> messageSupplier, Replaceable... replacements) {
        return option.orThrow(() -> new InternalValidationException(messageSupplier, replacements));
    }

}
