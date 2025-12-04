package net.dzikoysk.funnyguilds.shared.formatter;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FunnyFormatter implements Replaceable {

    private final List<Replacement<?>> replacements = new ArrayList<>();

    @Deprecated
    public String format(String message) {
        return this.replace(message);
    }

    // NOWA METODA – bez Locale
    public @NotNull String replace(@NotNull String text) {
        return this.replace(null, text);
    }

    @Override
    public @NotNull String replace(@Nullable Locale locale, @NotNull String text) {
        if (FunnyStringUtils.isEmpty(text)) {
            return "";
        }

        for (Replacement<?> replacement : this.replacements) {
            text = replacement.replaceInString(text);
        }

        return text;
    }

    @Override
    public @NotNull Component replace(@Nullable Locale locale, @NotNull Component text) {
        for (Replacement<?> replacement : this.replacements) {
            text = replacement.replaceInComponent(text);
        }

        return text;
    }

    public FunnyFormatter register(String placeholder, Component value) {
        this.replacements.add(new ComponentReplacement(placeholder, () -> value));
        return this;
    }

    public FunnyFormatter register(String placeholder, Object value) {
        this.replacements.add(new StringReplacement(placeholder, value::toString));
        return this;
    }

    public static FunnyFormatter of(String placeholder, Component value) {
        return new FunnyFormatter().register(placeholder, value);
    }

    public static FunnyFormatter of(String placeholder, Object value) {
        return new FunnyFormatter().register(placeholder, value);
    }

    // Tu dalej możesz korzystać z replace(String)
    public static String format(String text, String placeholder, Object value) {
        return new FunnyFormatter().register(placeholder, value).replace(text);
    }
}
