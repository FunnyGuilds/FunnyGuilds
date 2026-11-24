package net.dzikoysk.funnyguilds.feature.items;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsSnapshot;
import net.dzikoysk.funnyguilds.config.sections.items.SetRequirements;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult.ItemCountResult;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

public class GuildItemRequirementChecker {

    public ItemRequirementResult check(Player player, User user, GuildItemSet set, ItemsSnapshot config) {
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
            GuildCreationPayment payment = new GuildCreationPayment(player.getInventory(), set, config);
            Map<String, Integer> inventoryCounts = payment.getAvailableCounts();
            Map<String, Integer> enderCounts = payment.countMatching(player.getEnderChest());
            for (Map.Entry<String, Integer> entry : set.getItems().entrySet()) {
                String itemKey = entry.getKey();
                int required = entry.getValue();

                Optional<GuildItemDefinition> libraryItem = config.getLibraryItem(itemKey);

                String displayName = libraryItem
                        .map(def -> def.name)
                        .filter(name -> name != null && !name.isEmpty())
                        .orElse(itemKey);

                int inv = inventoryCounts.get(itemKey);
                int ender = enderCounts.get(itemKey);

                itemCounts.put(itemKey, new ItemCountResult(required, inv, ender, displayName));
            }
        }

        return new ItemRequirementResult(meetsMoney, meetsLevel, meetsRank, itemCounts);
    }

}
