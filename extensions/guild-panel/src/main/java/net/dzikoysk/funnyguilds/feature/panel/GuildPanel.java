package net.dzikoysk.funnyguilds.feature.panel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess.Relation;
import net.dzikoysk.funnyguilds.module.api.PanelView;
import net.dzikoysk.funnyguilds.module.api.PanelView.Button;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class GuildPanel {

    private final PanelModule module;
    private final Player player;
    private final GuildPanelAccess access;
    private final PanelAppearance appearance;
    private final PanelConfiguration configuration;
    private final PanelConfiguration.Overview screen;

    public GuildPanel(PanelModule module, Player player, GuildPanelAccess access,
                      MiniMessage miniMessage, BaseIconFactory icons) {
        this.module = module;
        this.player = player;
        this.access = access;
        this.appearance = new PanelAppearance(miniMessage, icons);
        this.configuration = module.configuration();
        this.screen = this.configuration.screens.overview;
    }

    public void open() {
        Map<String, String> values = new HashMap<>(this.access.overview());
        values.replaceAll((key, value) -> value.isEmpty() ? this.screen.emptyText : value);
        values.put("PVP", this.status(this.access.pvp()));
        List<Component> allies = this.access.allies().stream()
                .sorted(Comparator.comparing(Relation::tag, String.CASE_INSENSITIVE_ORDER))
                .map(ally -> this.render(this.screen.allianceLine, this.allyPvpValues(ally))).toList();
        this.show(this.screen.title, this.screen.rows, this.screen.pattern, values,
                key -> new Button(this.icon(this.screen.items.get(key), values, key.equals("allies") ? allies : List.of()),
                        () -> this.clickOverview(key)));
    }

    void show(String title, int rows, List<String> pattern, Map<String, String> values, Function<String, Button> buttons) {
        String renderedTitle = LegacyComponentSerializer.legacySection().serialize(this.render(title, values));
        this.show(new PanelView(renderedTitle, rows, pattern, buttons));
    }

    private void show(PanelView view) {
        this.module.show(this.player, view, this::isActive);
    }

    ItemStack icon(GuildItemDefinition definition, Map<String, String> values, List<Component> lore) {
        return this.icon(definition, values, lore, null);
    }

    ItemStack icon(GuildItemDefinition definition, Map<String, String> values, List<Component> lore, @Nullable String material) {
        return this.appearance.icon(definition, values, lore, material);
    }

    Component render(String template, Map<String, String> values) {
        return this.appearance.render(template, values);
    }

    Map<String, String> allyPvpValues(Relation ally) {
        return Map.of("ALLY", ally.name(), "ALLY_TAG", ally.tag(),
                "ALLY_PVP", this.status(this.access.allyPvpAllowed() && ally.ourPvp() && ally.theirPvp()),
                "OUR_PVP", this.status(ally.ourPvp()), "THEIR_PVP", this.status(ally.theirPvp()));
    }

    String status(boolean enabled) {
        return enabled ? this.screen.enabledText : this.screen.disabledText;
    }

    boolean isActive() {
        return this.module.configuration() == this.configuration && this.access.isActive();
    }

    private void clickOverview(String key) {
        switch (key) {
            case "close" -> this.player.closeInventory();
            case "allies" -> new GuildRelationsPanel(this, this.configuration, this.access).open();
            case "territory" -> new GuildPurchasesPanel(this, this.configuration, this.access).openExpansion(0);
            case "validity" -> new GuildPurchasesPanel(this, this.configuration, this.access).openRenewal(0);
            case "roles" -> new GuildRolesPanel(this, this.module, this.configuration, this.player, this.access).roles(0);
            case "audit" -> new GuildAuditPanel(this.configuration, this.access.audit(), this::show, this::open, this::render, this::icon).open();
            case "base" -> this.access.teleport();
            default -> { }
        }
    }

    @FunctionalInterface
    public interface BaseIconFactory {
        ItemStack create(GuildItemDefinition definition, @Nullable String material);
    }
}
