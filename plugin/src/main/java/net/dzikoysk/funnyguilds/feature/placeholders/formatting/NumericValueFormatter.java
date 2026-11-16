package net.dzikoysk.funnyguilds.feature.placeholders.formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.kyori.adventure.text.Component;

/** An immutable, ordered set of presentation rules. Values are never cached. */
public final class NumericValueFormatter {

    private final List<Rule> rules;

    public NumericValueFormatter(List<RangeFormatting> formats, String path) {
        if (formats == null) {
            throw new IllegalArgumentException(path + ": expected a list, use [] to disable formatting");
        }
        List<Rule> compiled = new ArrayList<>();
        for (int index = 0; index < formats.size(); index++) {
            try {
                RangeFormatting format = formats.get(index);
                format.getRange().validate();
                if (format.getValue() == null || format.getValue().isBlank()) {
                    throw new IllegalArgumentException("Expected a non-empty template");
                }
                compiled.add(new Rule(format.getRange(), ComponentUtil.colored(format.getValue())));
            }
            catch (RuntimeException exception) {
                throw new IllegalArgumentException(path + "[" + index + "]: " + exception.getMessage(), exception);
            }
        }
        this.rules = List.copyOf(compiled);
    }

    public Component format(Number value) {
        Component number = plain(value);
        for (Rule rule : this.rules) {
            if (rule.range().contains(value)) {
                return rule.template().replaceText(builder -> builder.matchLiteral("{VALUE}").replacement(number));
            }
        }
        return number;
    }

    public static Component plain(Number value) {
        return Component.text(value instanceof Float || value instanceof Double
                ? String.format(Locale.ROOT, "%.2f", value.doubleValue())
                : value.toString());
    }

    private record Rule(NumberRange range, Component template) { }
}
