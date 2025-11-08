package net.dzikoysk.funnyguilds.shared.formatter;

import java.util.function.Supplier;
import net.kyori.adventure.text.TextReplacementConfig;

public final class StringReplacement extends Replacement<String> {

    public StringReplacement(String placeholder, Supplier<String> valueSupplier) {
        super(placeholder, valueSupplier);
    }

    @Override
    public TextReplacementConfig prepareReplacementConfig(String value) {
        return TextReplacementConfig.builder().matchLiteral(this.placeholder).replacement(value).build();
    }
}
