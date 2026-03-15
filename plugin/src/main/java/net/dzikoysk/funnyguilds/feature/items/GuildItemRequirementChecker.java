package net.dzikoysk.funnyguilds.feature.items;

import java.util.LinkedHashMap;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import net.dzikoysk.funnyguilds.config.sections.items.SetRequirements;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult.ItemCountResult;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GuildItemRequirementChecker {

    public ItemRequirementResult check(Player player, User user, GuildItemSet set, ItemsConfiguration config) {
        SetRequirements req = set.requirements;

        boolean meetsMoney = true;
        if (req.moneyEnabled && set.requiredMoney > 0) {
            meetsMoney = VaultHook.isEconomyHooked() && VaultHook.canAfford(player, set.requiredMoney);
        }

        boolean meetsExperience = true;
        if (req.experienceEnabled && set.requiredExperience > 0) {
            meetsExperience = player.getLevel() >= set.requiredExperience;
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

                var libraryItem = config.getLibraryItem(itemKey);
                Material material = libraryItem
                        .map(def -> Material.matchMaterial(def.material))
                        .orElse(null);

                String displayName = libraryItem
                        .map(def -> def.name)
                        .filter(name -> name != null && !name.isEmpty())
                        .orElse(itemKey);

                int inv = 0;
                int ender = 0;

                if (material != null) {
                    inv = ItemUtils.getItemAmountByMaterial(material, player.getInventory());
                    ender = ItemUtils.getItemAmountByMaterial(material, player.getEnderChest());
                }

                itemCounts.put(itemKey, new ItemCountResult(required, inv, ender, displayName));
            }
        }

        return new ItemRequirementResult(meetsMoney, meetsExperience, meetsRank, itemCounts);
    }

    public ItemRequirementResult check(Player player, GuildItemSet set, ItemsConfiguration config) {
        return check(player, null, set, config);
    }

}

