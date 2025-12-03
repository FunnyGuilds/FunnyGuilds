package net.dzikoysk.funnyguilds.shared.formatter;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FunnyFormatter implements Replaceable {

    private final List<Replacement> replacements = new ArrayList<>();

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

    public FunnyFormatter register(@NotNull Replacement replacement) {
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

}
