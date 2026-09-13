package net.dzikoysk.funnyguilds.feature.upgrades.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.config.sections.upgrades.UpgradeDefinition;
import net.dzikoysk.funnyguilds.config.sections.upgrades.UpgradeLevelDefinition;
import net.dzikoysk.funnyguilds.config.upgrades.UpgradesMenuItemConfig;
import net.dzikoysk.funnyguilds.config.upgrades.UpgradesMenuConfiguration;
import net.dzikoysk.funnyguilds.feature.items.gui.GuiItemBuilder;
import net.dzikoysk.funnyguilds.feature.upgrades.GuildUpgradeService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.adventure.MiniLegacyHelper;
import net.dzikoysk.funnyguilds.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class GuildUpgradesGuiFactory {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");

    private GuildUpgradesGuiFactory() {}

    public static GuildUpgradesGui create(
            Player player,
            User user,
            Guild guild,
            UpgradesMenuConfiguration menuConfig,
            GuildUpgradeService upgradeService,
            MessageService messageService
    ) {
        return new GuildUpgradesGui(player, user, guild, menuConfig, upgradeService, messageService);
    }

    static ChestGui buildChestGui(GuildUpgradesGui gui) {
        UpgradesMenuConfiguration menuConfig = gui.getMenuConfig();

        Component titleComponent = MiniLegacyHelper.miniMessage().deserialize(menuConfig.title);
        String legacyTitle = LegacyComponentSerializer.legacySection().serialize(titleComponent);

        ChestGui chestGui = new ChestGui(menuConfig.rows, legacyTitle);
        chestGui.setOnGlobalClick(event -> event.setCancelled(true));

        populatePanes(gui, chestGui);
        return chestGui;
    }

    static void populatePanes(GuildUpgradesGui gui, ChestGui chestGui) {
        UpgradesMenuConfiguration menuConfig = gui.getMenuConfig();
        List<String> pattern = menuConfig.pattern;
        int rows = menuConfig.rows;

        StaticPane pane = new StaticPane(9, rows, Pane.Priority.NORMAL);

        for (int row = 0; row < Math.min(pattern.size(), rows); row++) {
            List<String> tokens = extractPlaceholders(pattern.get(row));

            for (int col = 0; col < Math.min(tokens.size(), 9); col++) {
                GuiItem guiItem = resolvePlaceholder(tokens.get(col), gui, menuConfig);
                if (guiItem != null) {
                    pane.addItem(guiItem, col, row);
                }
            }
        }

        chestGui.getPanes().clear();
        chestGui.addPane(Slot.fromXY(0, 0), pane);
    }

    private static List<String> extractPlaceholders(String line) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(line);
        while (matcher.find()) {
            tokens.add(matcher.group(1));
        }
        return tokens;
    }

    private static GuiItem resolvePlaceholder(String placeholder, GuildUpgradesGui gui, UpgradesMenuConfiguration menuConfig) {
        if (placeholder.startsWith("close:")) {
            return buildCloseItem(menuConfig, gui);
        }

        if (placeholder.equals("item-x")) {
            return buildFillerItem(menuConfig);
        }

        return buildUpgradeItem(placeholder, gui, menuConfig);
    }

    private static GuiItem buildFillerItem(UpgradesMenuConfiguration menuConfig) {
        ItemStack stack = GuiItemBuilder.buildSimple(menuConfig.filler);
        return new GuiItem(stack, event -> event.setCancelled(true));
    }

    private static GuiItem buildCloseItem(UpgradesMenuConfiguration menuConfig, GuildUpgradesGui gui) {
        ItemStack stack = GuiItemBuilder.buildSimple(menuConfig.close);
        return new GuiItem(stack, event -> {
            event.setCancelled(true);
            gui.close();
        });
    }

    private static GuiItem buildUpgradeItem(String key, GuildUpgradesGui gui, UpgradesMenuConfiguration menuConfig) {
        GuildUpgradeService service = gui.getUpgradeService();
        Guild guild = gui.getGuild();

        Optional<UpgradeDefinition> definition = service.getDefinition(key);
        if (definition.isEmpty()) {
            return buildFillerItem(menuConfig);
        }

        UpgradesMenuItemConfig item = menuConfig.getItem(key);
        int level = service.getLevel(guild, key);
        int maxLevel = service.getMaxLevel(key);
        boolean maxed = service.isMaxLevel(guild, key);

        double currentValue = service.getCurrentLevelDefinition(guild, key)
                .map(levelDefinition -> levelDefinition.value)
                .orElse(0.0);
        Optional<UpgradeLevelDefinition> nextLevelDefinition = service.getNextLevelDefinition(guild, key);
        double nextValue = nextLevelDefinition.map(levelDefinition -> levelDefinition.value).orElse(currentValue);
        String cost = nextLevelDefinition.map(levelDefinition -> formatNumber(levelDefinition.vaultCost)).orElse("-");

        List<String> loreLines = maxed ? item.maxedLore : item.lore;

        GuildItemDefinition itemDefinition = new GuildItemDefinition(item.material);
        itemDefinition.name = item.name;

        List<String> resolvedLore = new ArrayList<>();
        if (loreLines != null) {
            for (String line : loreLines) {
                resolvedLore.add(line
                        .replace("{LEVEL}", String.valueOf(level))
                        .replace("{MAX_LEVEL}", String.valueOf(maxLevel))
                        .replace("{VALUE}", formatNumber(currentValue))
                        .replace("{NEXT_VALUE}", formatNumber(nextValue))
                        .replace("{COST}", cost));
            }
        }
        itemDefinition.lore = resolvedLore;

        ItemStack stack = GuiItemBuilder.buildSimple(itemDefinition);

        return new GuiItem(stack, event -> {
            event.setCancelled(true);
            gui.attemptPurchase(key);
        });
    }

    private static String formatNumber(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }

}
