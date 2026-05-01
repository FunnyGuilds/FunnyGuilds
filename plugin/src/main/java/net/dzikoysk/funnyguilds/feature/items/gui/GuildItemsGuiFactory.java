package net.dzikoysk.funnyguilds.feature.items.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemToggleDefinition;
import net.dzikoysk.funnyguilds.config.sections.items.GuiConfiguration;
import net.dzikoysk.funnyguilds.config.sections.items.GuiConfiguration.ItemDisplayGroup;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import net.dzikoysk.funnyguilds.config.sections.items.PerSetConfig;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.items.GuildItemRequirementChecker;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult.ItemCountResult;
import net.dzikoysk.funnyguilds.shared.adventure.MiniLegacyHelper;
import net.dzikoysk.funnyguilds.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GuildItemsGuiFactory {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\{([^}]+)}");

    private GuildItemsGuiFactory() {}

    public static GuildItemsGui create(
            Player player,
            User user,
            GuildItemSet set,
            String setName,
            ItemsConfiguration config,
            MessageService messageService
    ) {
        return new GuildItemsGui(player, user, set, setName, config, messageService);
    }

    static ChestGui buildChestGui(GuildItemsGui gui) {
        GuiConfiguration guiConfig = gui.getConfig().gui;
        PerSetConfig perSet = guiConfig.getPerSetConfig(gui.getSetName());

        String rawTitle = perSet.hasTitle() ? perSet.title : guiConfig.title;
        if (rawTitle == null) rawTitle = gui.getSetName();
        String resolvedTitle = rawTitle.replace("{SET}", gui.getSetName());
        Component titleComponent = MiniLegacyHelper.miniMessage().deserialize(resolvedTitle);
        String legacyTitle = LegacyComponentSerializer.legacySection().serialize(titleComponent);

        ChestGui chestGui = new ChestGui(guiConfig.rows, legacyTitle);
        chestGui.setOnGlobalClick(event -> event.setCancelled(true));

        populatePanes(gui, chestGui);
        return chestGui;
    }

    static void populatePanes(GuildItemsGui gui, ChestGui chestGui) {
        ItemsConfiguration config = gui.getConfig();
        GuiConfiguration guiConfig = config.gui;
        PerSetConfig perSet = guiConfig.getPerSetConfig(gui.getSetName());
        GuildItemSet set = gui.getSet();
        Player player = gui.getPlayer();

        GuildItemRequirementChecker checker = new GuildItemRequirementChecker();
        ItemRequirementResult result = checker.check(player, gui.getUser(), set, config);

        ItemDisplayGroup displayGroup = guiConfig.itemDisplay;

        List<String> setItemKeys = new ArrayList<>(set.getItems().keySet());

        List<String> pattern = guiConfig.pattern;
        int rows = guiConfig.rows;

        StaticPane pane = new StaticPane(0, 0, 9, rows, Pane.Priority.NORMAL);

        for (int row = 0; row < Math.min(pattern.size(), rows); row++) {
            String line = pattern.get(row);
            List<String> tokens = extractTokens(line);

            for (int col = 0; col < Math.min(tokens.size(), 9); col++) {
                String token = tokens.get(col);
                GuiItem guiItem = resolveToken(token, set, setItemKeys, result,
                        displayGroup, perSet, config, gui);
                if (guiItem != null) {
                    pane.addItem(guiItem, col, row);
                }
            }
        }

        chestGui.addPane(pane);
    }

    static List<String> extractTokens(String line) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(line);
        while (matcher.find()) {
            tokens.add(matcher.group(1)); // zawartość bez { }
        }
        return tokens;
    }

    private static GuiItem resolveToken(
            String token,
            GuildItemSet set,
            List<String> setItemKeys,
            ItemRequirementResult result,
            ItemDisplayGroup displayGroup,
            PerSetConfig perSet,
            ItemsConfiguration config,
            GuildItemsGui gui
    ) {
        if (token.startsWith("toggle:")) {
            String key = token.substring("toggle:".length());
            return buildToggleItem(key, config, perSet, gui);
        }

        if (token.startsWith("summary:")) {
            String key = token.substring("summary:".length());
            return buildSummaryItem(key, config, perSet, set, result, gui);
        }

        if (token.startsWith("close:")) {
            String key = token.substring("close:".length());
            return buildCloseItem(key, config, perSet, gui);
        }

        if (token.matches("item-\\d+")) {
            int index = Integer.parseInt(token.substring("item-".length())) - 1;
            if (index < 0 || index >= setItemKeys.size()) {
                return buildFillerItem("item-x", config, perSet);
            }
            String itemKey = setItemKeys.get(index);
            int required = set.getItems().getOrDefault(itemKey, 1);
            ItemCountResult counts = result.getItemCounts().get(itemKey);
            if (counts == null) counts = new ItemCountResult(required, 0, 0);

            boolean met = counts.isMet();

            if (gui.isShowOnlyMissing() && met) {
                return buildFillerItem("item-x", config, perSet);
            }

            GuildItemDefinition def = config.resolveItem(itemKey, perSet).orElse(null);
            if (def == null) return buildFillerItem("item-x", config, perSet);

            ItemStack stack = GuiItemBuilder.buildWithDisplay(
                    def,
                    met ? displayGroup.hasEnough : displayGroup.missing,
                    counts
            );
            return new GuiItem(stack, event -> event.setCancelled(true));
        }

        return buildFillerItem(token, config, perSet);
    }

    private static GuiItem buildFillerItem(String key, ItemsConfiguration config, PerSetConfig perSet) {
        GuildItemDefinition def = config.resolveItem(key, perSet)
                .orElseGet(GuildItemsGuiFactory::fallbackDef);
        ItemStack stack = GuiItemBuilder.buildSimple(def);
        return new GuiItem(stack, event -> event.setCancelled(true));
    }

    private static GuiItem buildToggleItem(String key, ItemsConfiguration config, PerSetConfig perSet, GuildItemsGui gui) {
        GuildItemToggleDefinition toggle = config.getToggleItem(key).orElse(null);

        ItemStack stack;
        if (toggle != null) {
            GuildItemDefinition def = gui.isShowOnlyMissing() ? toggle.missingOnly : toggle.all;
            stack = GuiItemBuilder.buildSimple(def);
        } else {
            GuildItemDefinition def = config.resolveItem(key, perSet).orElseGet(GuildItemsGuiFactory::fallbackDef);
            stack = GuiItemBuilder.buildSimple(def);
        }

        return new GuiItem(stack, event -> {
            event.setCancelled(true);
            gui.toggleView();
        });
    }

    private static GuiItem buildSummaryItem(
            String key,
            ItemsConfiguration config,
            PerSetConfig perSet,
            GuildItemSet set,
            ItemRequirementResult result,
            GuildItemsGui gui
    ) {
        GuildItemDefinition def = config.resolveItem(key, perSet).orElseGet(GuildItemsGuiFactory::fallbackDef);
        ItemStack base = GuiItemBuilder.buildSimple(def);

        ItemMeta meta = base.getItemMeta();
        if (meta != null) {
            List<Component> lore = new ArrayList<>();
            MessageService messageService = gui.getMessageService();
            Player player = gui.getPlayer();

            if (def.lore != null) {
                for (String line : def.lore) {
                    switch (line) {
                        case "{MONEY_LINE}" -> {
                            if (set.requirements.moneyEnabled) {
                                double current = VaultHook.isEconomyHooked()
                                        ? VaultHook.accountBalance(player) : 0;
                                String statusColor = result.isMeetsMoney() ? "<green>" : "<red>";
                                String template = messageService.get(player, cfg -> cfg.itemsGuiMoneyLine)
                                        .replace("{STATUS_COLOR}", statusColor)
                                        .replace("{CURRENT}", formatMoney(current))
                                        .replace("{REQUIRED}", formatMoney(set.requiredMoney));
                                lore.add(mm(template));
                            }
                        }
                        case "{LEVEL_LINE}" -> {
                            if (set.requirements.levelEnabled) {
                                int current = player.getLevel();
                                String statusColor = result.isMeetsLevel() ? "<green>" : "<red>";
                                String template = messageService.get(player, cfg -> cfg.itemsGuiLevelLine)
                                        .replace("{STATUS_COLOR}", statusColor)
                                        .replace("{CURRENT}", String.valueOf(current))
                                        .replace("{REQUIRED}", String.valueOf(set.requiredLevel));
                                lore.add(mm(template));
                            }
                        }
                        case "{RANK_LINE}" -> {
                            if (set.requirements.rankEnabled && gui.getUser() != null) {
                                int current = gui.getUser().getRank().getPoints();
                                String statusColor = result.isMeetsRank() ? "<green>" : "<red>";
                                String template = messageService.get(player, cfg -> cfg.itemsGuiRankLine)
                                        .replace("{STATUS_COLOR}", statusColor)
                                        .replace("{CURRENT}", String.valueOf(current))
                                        .replace("{REQUIRED}", String.valueOf(set.requiredRank));
                                lore.add(mm(template));
                            }
                        }
                        case "{STATUS}" -> {
                            boolean ready = result.meetsAll();
                            String template = ready
                                    ? messageService.get(player, cfg -> cfg.itemsGuiStatusReady)
                                    : messageService.get(player, cfg -> cfg.itemsGuiStatusNotReady);
                            lore.add(mm(template));
                        }
                        default -> lore.add(mm(line));
                    }
                }
            }

            meta.lore(lore);
            base.setItemMeta(meta);
        }

        return new GuiItem(base, event -> event.setCancelled(true));
    }

    private static GuiItem buildCloseItem(String key, ItemsConfiguration config, PerSetConfig perSet, GuildItemsGui gui) {
        GuildItemDefinition def = config.resolveItem(key, perSet).orElseGet(GuildItemsGuiFactory::fallbackDef);
        ItemStack stack = GuiItemBuilder.buildSimple(def);
        return new GuiItem(stack, event -> {
            event.setCancelled(true);
            gui.close();
        });
    }

    private static GuildItemDefinition fallbackDef() {
        GuildItemDefinition def = new GuildItemDefinition("BLACK_STAINED_GLASS_PANE");
        def.name = " ";
        return def;
    }

    private static Component mm(String text) {
        return MiniLegacyHelper.miniMessage().deserialize(text);
    }

    private static String formatMoney(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }

}

