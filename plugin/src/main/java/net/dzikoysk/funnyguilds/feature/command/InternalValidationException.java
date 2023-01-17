package net.dzikoysk.funnyguilds.feature.command;

import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import pl.peridot.yetanothermessageslibrary.message.Sendable;
import pl.peridot.yetanothermessageslibrary.replace.Replaceable;

public class InternalValidationException extends RuntimeException {

    private final Function<MessageConfiguration, Sendable> messageSupplier;
    private final Replaceable[] replacements;

    public InternalValidationException(Function<MessageConfiguration, Sendable> messageSupplier, Replaceable... replacements) {
        super(null, null, false, false);
        this.messageSupplier = messageSupplier;
        this.replacements = replacements;
    }

    public Function<MessageConfiguration, Sendable> getMessageSupplier() {
        return this.messageSupplier;
    }

    public Replaceable[] getReplacements() {
        return this.replacements;
    }

}
