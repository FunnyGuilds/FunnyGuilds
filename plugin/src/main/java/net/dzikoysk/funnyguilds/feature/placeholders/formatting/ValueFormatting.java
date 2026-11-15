package net.dzikoysk.funnyguilds.feature.placeholders.formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.config.sections.TopConfiguration;
import net.dzikoysk.funnyguilds.config.sections.ValueFormattingConfiguration;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.kyori.adventure.text.Component;

/** A complete snapshot, published only after every rule has been compiled successfully. */
public final class ValueFormatting {

    private static final NumericValueFormatter PLAIN = new NumericValueFormatter(List.of(), "default");

    private final Map<String, NumericValueFormatter> user;
    private final Map<String, NumericValueFormatter> guild;
    private final Map<String, NumericValueFormatter> userTop;
    private final Map<String, NumericValueFormatter> guildTop;
    private final NumericValueFormatter ping;
    private final Component userTopTemplate;
    private final Component guildTopTemplate;

    public ValueFormatting(ValueFormattingConfiguration config, TopConfiguration top) {
        this.user = compile(config.user, "value-formatting.user");
        this.guild = compile(config.guild, "value-formatting.guild");
        this.ping = new NumericValueFormatter(config.ping, "value-formatting.ping");
        this.userTop = compile(top.format.ptopValueFormatting, "top.format.ptop-value-formatting");
        this.guildTop = compile(top.format.gtopValueFormatting, "top.format.gtop-value-formatting");
        this.userTopTemplate = ComponentUtil.colored(top.format.ptop);
        this.guildTopTemplate = ComponentUtil.colored(top.format.gtop);
    }

    private static Map<String, NumericValueFormatter> compile(Map<String, List<RangeFormatting>> formats, String path) {
        if (formats == null) {
            throw new IllegalArgumentException(path + ": expected a map");
        }
        Map<String, NumericValueFormatter> compiled = new HashMap<>();
        formats.forEach((key, rules) -> {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException(path + ": expected a non-empty metric id");
            }
            if (compiled.putIfAbsent(key.toLowerCase(Locale.ROOT), new NumericValueFormatter(rules, path + "." + key)) != null) {
                throw new IllegalArgumentException(path + ": duplicate metric id " + key);
            }
        });
        return Map.copyOf(compiled);
    }

    public Component user(String metric, Number value) {
        return this.user.getOrDefault(metric.toLowerCase(Locale.ROOT), PLAIN).format(value);
    }

    public Component guild(String metric, Number value) {
        return this.guild.getOrDefault(metric.toLowerCase(Locale.ROOT), PLAIN).format(value);
    }

    public Component ping(Number value) {
        return this.ping.format(value);
    }

    public Component top(boolean guild, String metric, Number value, String rawValue) {
        String id = metric.toLowerCase(Locale.ROOT);
        Map<String, NumericValueFormatter> overrides = guild ? this.guildTop : this.userTop;
        Map<String, NumericValueFormatter> defaults = guild ? this.guild : this.user;
        NumericValueFormatter formatter = overrides.getOrDefault(id, defaults.getOrDefault(id, PLAIN));
        Component template = guild ? this.guildTopTemplate : this.userTopTemplate;
        // A single pass keeps tokens inside a configured rule local to that rule.
        return template.replaceText(builder -> builder.match("\\{VALUE(?:-FORMAT)?}")
                .replacement((match, text) -> match.group().equals("{VALUE}")
                        ? Component.text(rawValue)
                        : formatter.format(value)));
    }
}
