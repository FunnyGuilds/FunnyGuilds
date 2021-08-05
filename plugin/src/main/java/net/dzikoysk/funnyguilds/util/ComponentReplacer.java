package net.dzikoysk.funnyguilds.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

public class ComponentReplacer {
    private Component component;

    @Contract("null -> fail")
    public ComponentReplacer(ComponentLike componentLike) {
        this.component = Objects.requireNonNull(componentLike.asComponent(), "componentLike");
    }

    @Contract("_, null -> fail; null, _ -> fail")
    public ComponentReplacer replace(String search, Object replace) {
        Component replacement = Component.text("<?>");
        TextReplacementConfig.Builder replacementConfigBuilder = TextReplacementConfig.builder().matchLiteral(search);

        if (replace instanceof ComponentLike) {
            replacement = (Component) replace;
        }

        this.component = this.component.replaceText(replacementConfigBuilder.replacement(replacement).build());
        return this;
    }

    @Contract("-> new")
    public Component finish() {
        return this.component;
    }
}
