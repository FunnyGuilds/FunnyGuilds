package net.dzikoysk.funnyguilds.feature.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

final class PanelAppearance {

    private final MiniMessage miniMessage;
    private final GuildPanel.BaseIconFactory icons;

    PanelAppearance(MiniMessage miniMessage, GuildPanel.BaseIconFactory icons) {
        this.miniMessage = miniMessage;
        this.icons = icons;
    }

    ItemStack icon(GuildItemDefinition definition, Map<String, String> values, List<Component> extraLore, @Nullable String material) {
        ItemStack item = this.icons.create(definition, material);
        ItemMeta meta = item.getItemMeta();
        if (!definition.name.isEmpty()) {
            meta.displayName(render(definition.name, values));
        }
        List<Component> lore = new ArrayList<>();
        definition.lore.forEach(line -> lore.add(render(line, values)));
        lore.addAll(extraLore);
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    Component render(String template, Map<String, String> values) {
        TagResolver.Builder resolver = TagResolver.builder();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String tag = "panel_" + entry.getKey().toLowerCase(Locale.ROOT);
            template = template.replace("{" + entry.getKey() + "}", "<" + tag + ">");
            resolver.resolver(Placeholder.unparsed(tag, entry.getValue()));
        }
        return this.miniMessage.deserialize(template, resolver.build()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

}
