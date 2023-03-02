package net.dzikoysk.funnyguilds.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Pair;
import dev.peri.yetanothermessageslibrary.replace.Replaceable;

public final class FunnyFormatter implements Replaceable {

    private final List<Pair<String, Supplier<?>>> placeholders = new ArrayList<>();

    public String format(String message) {
        if (FunnyStringUtils.isEmpty(message)) {
            return "";
        }

        for (Pair<String, Supplier<?>> placeholderPair : this.placeholders) {
            message = format(message, placeholderPair.getFirst(), placeholderPair.getSecond());
        }

        return message;
    }

    public static String format(String message, String placeholder, Object value) {
        return format(message, placeholder, value::toString);
    }

    public static String format(String message, String placeholder, Supplier<?> valueSupplier) {
        if (FunnyStringUtils.isEmpty(message)) {
            return "";
        }

        if (!message.contains(placeholder)) {
            return message;
        }

        Object value = valueSupplier.get();
        if (value == null) {
            throw new NullPointerException("Placeholder " + placeholder + " returns null value");
        }

        return FunnyStringUtils.replace(message, placeholder, Objects.toString(value));
    }

    @Override
    public @NotNull String replace(@Nullable Locale locale, @NotNull String text) {
        return this.format(text);
    }

    @Override
    public @NotNull Component replace(@Nullable Locale locale, @NotNull Component text) {
        for (Pair<String, Supplier<?>> placeholderPair : this.placeholders) {
            String placeholder = placeholderPair.getFirst();
            Object value = placeholderPair.getSecond().get();
            if (value == null) {
                throw new NullPointerException("Placeholder " + placeholder + " returns null value");
            }

            TextReplacementConfig replacement = TextReplacementConfig.builder()
                    .matchLiteral(placeholder)
                    .replacement(Objects.toString(value))
                    .build();

            text = text.replaceText(replacement);
        }

        return text;
    }

    public FunnyFormatter register(String placeholder, Object value) {
        return this.register(placeholder, value::toString);
    }

    public FunnyFormatter register(String placeholder, Supplier<?> valueSupplier) {
        this.placeholders.add(Pair.of(placeholder, valueSupplier));
        return this;
    }

    public static FunnyFormatter of(String placeholder, Object value) {
        return of(placeholder, value::toString);
    }

    public static FunnyFormatter of(String placeholder, Supplier<?> valueSupplier) {
        return new FunnyFormatter().register(placeholder, valueSupplier);
    }

}
