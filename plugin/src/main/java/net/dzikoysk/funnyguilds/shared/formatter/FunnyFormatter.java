package net.dzikoysk.funnyguilds.shared.formatter;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FunnyFormatter implements Replaceable {

    private final List<Replaceable> replacements = new ArrayList<>();

    @Override
    public @NotNull Component replace(
            @Nullable Locale locale,
            @NotNull Component text
    ) {
        Component result = text;
        for (Replaceable replacement : this.replacements) {
            result = replacement.replace(locale, result);
        }
        return result;
    }

    public FunnyFormatter register(@NotNull Replaceable replacement) {
        this.replacements.add(replacement);
        return this;
    }
    
    public FunnyFormatter register(String placeholder, Object value) {
        return this.register(Replacement.string(placeholder, () -> Objects.toString(value)));
    }
    
    public FunnyFormatter register(String placeholder, String value) {
        return this.register(Replacement.string(placeholder, value));
    }
    
    public FunnyFormatter register(String placeholder, ComponentLike value) {
        return this.register(Replacement.component(placeholder, value));
    }

    public <T> FunnyFormatter register(Placeholders<T, ?> placeholders, T data) {
        return this.register(placeholders, data, "{", "}", name -> name.toLowerCase(Locale.ROOT));
    }
        
    public <T> FunnyFormatter register(Placeholders<T, ?> placeholders, T data, String prefix, String suffix, UnaryOperator<String> nameModifier) {
        placeholders.getPlaceholders().forEach((name, placeholder) -> this.register(Replacement.component(
                prefix + nameModifier.apply(name) + suffix,
                locale -> ComponentUtil.toComponent(placeholder.get(locale, data))
        )));
        return this;
    }
}
