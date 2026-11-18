package net.dzikoysk.funnyguilds.feature.panel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.module.api.PanelAudit;
import net.dzikoysk.funnyguilds.module.api.PanelView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration.PanelScreen;
import net.dzikoysk.funnyguilds.feature.items.gui.GuiPattern;
import net.dzikoysk.funnyguilds.module.api.PanelView.Button;

public final class GuildAuditPanel {

    private final PanelConfiguration.Screens screens;
    private final PanelConfiguration.Overview overview;
    private final PanelAudit audit;
    private final Consumer<PanelView> showView;
    private final Runnable back;
    private final BiFunction<String, Map<String, String>, Component> render;
    private final IconFactory icons;

    public GuildAuditPanel(PanelConfiguration config, PanelAudit audit, Consumer<PanelView> showView, Runnable back,
                           BiFunction<String, Map<String, String>, Component> render, IconFactory icons) {
        this.screens = config.screens;
        this.overview = config.screens.overview;
        this.audit = audit;
        this.showView = showView;
        this.back = back;
        this.render = render;
        this.icons = icons;
    }

    public void open() {
        this.open(0, 0);
    }

    private void open(int filter, int page) {
        this.audit.read().ifPresent(entries -> this.show(entries, filter, page));
    }

    private void show(List<PanelAudit.Entry> entries, int filter, int page) {
        List<String> categories = this.audit.categories();
        String selected = filter == 0 ? null : categories.get(filter - 1);
        PanelScreen screen = this.screens.audit;
        List<PanelAudit.Entry> filtered = entries.stream()
                .filter(entry -> selected == null || selected.equals(entry.category())).toList();
        int capacity = (int) screen.pattern.stream().flatMap(row -> GuiPattern.slots(row).stream()).filter("entry"::equals).count();
        int pages = Math.max(1, Math.ceilDiv(filtered.size(), capacity));
        int current = Math.min(page, pages - 1);
        var pageEntries = filtered.subList(current * capacity, Math.min(filtered.size(), (current + 1) * capacity)).iterator();
        Map<String, String> values = Map.of(
                "TAG", this.audit.guildTag(),
                "FILTER", selected == null ? this.screens.auditCategories.getOrDefault("all", "all") : this.category(selected),
                "PAGE", Integer.toString(current + 1), "PAGES", Integer.toString(pages));
        var emptyIcon = (filtered.isEmpty() && screen.items.containsKey("empty-log"))
                ? List.of(button(screen, "empty-log", values, null)).iterator() : List.<Button>of().iterator();

        String title = LegacyComponentSerializer.legacySection().serialize(this.render.apply(screen.title, values));
        this.showView.accept(new PanelView(title, screen.rows, screen.pattern, key -> switch (key) {
            case "entry" -> pageEntries.hasNext() ? this.entry(screen, pageEntries.next()) : emptyIcon.hasNext() ? emptyIcon.next() : null;
            case "filter" -> button(screen, key, values, () -> this.open((filter + 1) % (categories.size() + 1), 0));
            case "back" -> button(screen, key, values, this.back);
            case "previous" -> current > 0 ? button(screen, key, values, () -> this.open(filter, current - 1)) : null;
            case "next" -> current + 1 < pages ? button(screen, key, values, () -> this.open(filter, current + 1)) : null;
            default -> button(screen, key, values, null);
        }));
    }

    private Button entry(PanelScreen screen, PanelAudit.Entry entry) {
        Map<String, String> values = new HashMap<>(entry.details());
        values.computeIfPresent("STATE", (key, state) -> Boolean.parseBoolean(state) ? this.overview.enabledText : this.overview.disabledText);
        values.put("TAG", this.audit.guildTag());
        values.put("DATE", entry.date());
        values.put("ACTOR", entry.actor());
        values.put("CATEGORY", this.category(entry.category()));
        String description = this.screens.auditDescriptions.getOrDefault(entry.type(), entry.type());
        return new Button(this.icons.create(screen.items.get("entry"), values, List.of(this.render.apply(description, values))), null);
    }

    private String category(String category) {
        return this.screens.auditCategories.getOrDefault(category, category);
    }

    private Button button(PanelScreen screen, String key, Map<String, String> values, Runnable action) {
        return new Button(this.icons.create(screen.items.get(key), values, List.of()), action);
    }

    @FunctionalInterface
    public interface IconFactory {
        ItemStack create(GuildItemDefinition definition, Map<String, String> values, List<Component> extraLore);
    }
}
