package net.dzikoysk.funnyguilds.shared.formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.peri.yetanothermessageslibrary.replace.Replaceable;

public final class FunnyFormatter implements Replaceable {

    private final List<Replacement<?>> replacements = new ArrayList<>();

    public String format(String message) {
        if (FunnyStringUtils.isEmpty(message)) {
            return "";
        }

        for (Replacement<?> replacement : this.replacements) {
            message = replacement.replaceInString(message);
        }

        return message;
    }

    @Override
    public @NotNull String replace(@Nullable Locale locale, @NotNull String text) {
        return this.format(text);
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

    public static String format(String text, String placeholder, Object value) {
        return new FunnyFormatter().register(placeholder, value).replace(text);
    }
}
