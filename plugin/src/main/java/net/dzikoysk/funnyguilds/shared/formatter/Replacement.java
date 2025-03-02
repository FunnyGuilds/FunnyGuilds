package net.dzikoysk.funnyguilds.shared.formatter;

import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class Replacement<T> {

    protected final String placeholder;
    private final Supplier<T> valueSupplier;

    protected Replacement(String placeholder, Supplier<T> valueSupplier) {
        this.placeholder = placeholder;
        this.valueSupplier = valueSupplier;
    }

    public String replaceInString(@Nullable String input) {
        if (FunnyStringUtils.isEmpty(input)) {
            return "";
        }

        if (!input.contains(this.placeholder)) {
            return input;
        }

        T value = this.valueSupplier.get();
        if (value == null) {
            throw new NullPointerException("Placeholder " + placeholder + " returned null value");
        }

        return FunnyStringUtils.replace(input, this.placeholder, Objects.toString(this.valueSupplier.get()));
    }

    public Component replaceInComponent(@NotNull Component input) {
        T value = this.valueSupplier.get();
        if (value == null) {
            throw new NullPointerException("Placeholder " + placeholder + " returned null value");
        }

        TextReplacementConfig config = prepareReplacementConfig(value);
        return input.replaceText(config);
    }

    public abstract TextReplacementConfig prepareReplacementConfig(T value);
}
