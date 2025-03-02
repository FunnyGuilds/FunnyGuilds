package net.dzikoysk.funnyguilds.shared.formatter;

import net.kyori.adventure.text.TextReplacementConfig;

import java.util.function.Supplier;

public final class StringReplacement extends Replacement<String> {

    public StringReplacement(String placeholder, Supplier<String> valueSupplier) {
        super(placeholder, valueSupplier);
    }

    @Override
    public TextReplacementConfig prepareReplacementConfig(String value) {
        return TextReplacementConfig.builder()
                .matchLiteral(this.placeholder)
                .replacement(value)
                .build();
    }
}
