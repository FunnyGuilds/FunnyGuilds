package net.dzikoysk.funnyguilds.feature.items;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import net.dzikoysk.funnyguilds.config.sections.items.SetRequirements;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult.ItemCountResult;
import net.dzikoysk.funnyguilds.feature.items.gui.GuiItemBuilder;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GuildItemRequirementChecker {

    public ItemRequirementResult check(Player player, User user, GuildItemSet set, ItemsConfiguration config) {
        SetRequirements req = set.requirements;

        boolean meetsMoney = true;
        if (req.moneyEnabled && set.requiredMoney > 0) {
            meetsMoney = VaultHook.isEconomyHooked() && VaultHook.canAfford(player, set.requiredMoney);
        }

        boolean meetsLevel = true;
        if (req.levelEnabled && set.requiredLevel > 0) {
            meetsLevel = player.getLevel() >= set.requiredLevel;
        }

        boolean meetsRank = true;
        if (req.rankEnabled && set.requiredRank > 0 && user != null) {
            meetsRank = user.getRank().getPoints() >= set.requiredRank;
        }

        Map<String, ItemCountResult> itemCounts = new LinkedHashMap<>();
        if (req.itemsEnabled) {
            for (Map.Entry<String, Integer> entry : set.getItems().entrySet()) {
                String itemKey = entry.getKey();
                int required = entry.getValue();

                Optional<GuildItemDefinition> libraryItem = config.getLibraryItem(itemKey);

                String displayName = libraryItem
                        .map(def -> def.name)
                        .filter(name -> name != null && !name.isEmpty())
                        .orElse(itemKey);

                int inv = 0;
                int ender = 0;

                if (libraryItem.isPresent()) {
                    ItemStack template = GuiItemBuilder.toItemStack(libraryItem.get(), 1);
                    inv = ItemUtils.getItemAmount(template, player.getInventory());
                    ender = ItemUtils.getItemAmount(template, player.getEnderChest());
                }

                itemCounts.put(itemKey, new ItemCountResult(required, inv, ender, displayName));
            }
        }

        return new ItemRequirementResult(meetsMoney, meetsLevel, meetsRank, itemCounts);
    }

}
