package net.dzikoysk.funnyguilds.shared.formatter;

import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

public final class ComponentReplacement extends Replacement<Component> {

    public ComponentReplacement(String placeholder, Supplier<Component> valueSupplier) {
        super(placeholder, valueSupplier);
    }

    @Override
    public TextReplacementConfig prepareReplacementConfig(Component value) {
        return TextReplacementConfig.builder()
                .matchLiteral(this.placeholder)
                .replacement(value)
                .build();
    }
}
