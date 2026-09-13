package net.dzikoysk.funnyguilds.feature.upgrades;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.sections.upgrades.GuildUpgradeType;
import net.dzikoysk.funnyguilds.config.sections.upgrades.GuildUpgradesConfiguration;
import net.dzikoysk.funnyguilds.config.sections.upgrades.UpgradeDefinition;
import net.dzikoysk.funnyguilds.config.sections.upgrades.UpgradeLevelDefinition;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildUpgradePurchaseEvent;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class GuildUpgradeService {

    private final PluginConfiguration config;
    private final UpgradeItemStore upgradeItemStore;

    public GuildUpgradeService(PluginConfiguration config, UpgradeItemStore upgradeItemStore) {
        this.config = config;
        this.upgradeItemStore = upgradeItemStore;
    }

    public boolean isEnabled() {
        return this.config.guildUpgrades.enabled;
    }

    public GuildUpgradesConfiguration.EconomyType getEconomyType() {
        return this.config.guildUpgrades.economyType;
    }

    public Optional<UpgradeDefinition> getDefinition(String key) {
        return this.config.guildUpgrades.findUpgrade(key);
    }

    public int getLevel(Guild guild, String key) {
        return guild.getUpgradeLevel(key);
    }

    public int getMaxLevel(String key) {
        return this.getDefinition(key).map(UpgradeDefinition::getMaxLevel).orElse(0);
    }

    public boolean isMaxLevel(Guild guild, String key) {
        int maxLevel = this.getMaxLevel(key);
        return maxLevel > 0 && this.getLevel(guild, key) >= maxLevel;
    }

    public Optional<UpgradeLevelDefinition> getCurrentLevelDefinition(Guild guild, String key) {
        int level = this.getLevel(guild, key);
        if (level <= 0) {
            return Optional.empty();
        }

        return this.getDefinition(key).flatMap(definition -> definition.getLevel(level));
    }

    public Optional<UpgradeLevelDefinition> getNextLevelDefinition(Guild guild, String key) {
        int nextLevel = this.getLevel(guild, key) + 1;
        return this.getDefinition(key).flatMap(definition -> definition.getLevel(nextLevel));
    }

    public double getPointsBoostMultiplier(@Nullable Guild guild) {
        if (guild == null || !this.isEnabled()) {
            return 1.0;
        }

        return 1.0 + (this.sumValueForType(guild, GuildUpgradeType.POINTS_BOOST) / 100.0);
    }

    public double getBonusMaxMembers(@Nullable Guild guild) {
        if (guild == null || !this.isEnabled()) {
            return 0.0;
        }

        return this.sumValueForType(guild, GuildUpgradeType.MEMBERS);
    }

    public int getMaxMembers(@Nullable Guild guild) {
        return this.config.maxMembersInGuild + (int) Math.round(this.getBonusMaxMembers(guild));
    }

    public int getMaxPossibleMemberBonus() {
        double max = 0.0;

        for (Map.Entry<String, UpgradeDefinition> entry : this.config.guildUpgrades.getUpgrades().entrySet()) {
            if (GuildUpgradeType.fromKey(entry.getKey()).filter(resolved -> resolved == GuildUpgradeType.MEMBERS).isEmpty()) {
                continue;
            }

            List<UpgradeLevelDefinition> levels = entry.getValue().getLevels();
            if (!levels.isEmpty()) {
                max += levels.getLast().value;
            }
        }

        return (int) Math.round(max);
    }

    private double sumValueForType(Guild guild, GuildUpgradeType type) {
        double total = 0.0;

        for (Map.Entry<String, UpgradeDefinition> entry : this.config.guildUpgrades.getUpgrades().entrySet()) {
            if (GuildUpgradeType.fromKey(entry.getKey()).filter(resolved -> resolved == type).isEmpty()) {
                continue;
            }

            int level = guild.getUpgradeLevel(entry.getKey());
            if (level <= 0) {
                continue;
            }

            Optional<UpgradeLevelDefinition> levelDefinition = entry.getValue().getLevel(level);
            if (levelDefinition.isPresent()) {
                total += levelDefinition.get().value;
            }
        }

        return total;
    }

    public PurchaseResult purchase(Player player, User user, Guild guild, String key) {
        if (!this.isEnabled()) {
            return PurchaseResult.DISABLED;
        }

        Optional<UpgradeDefinition> definitionOption = this.getDefinition(key);
        if (definitionOption.isEmpty() || GuildUpgradeType.fromKey(key).isEmpty()) {
            return PurchaseResult.UNKNOWN_UPGRADE;
        }

        int nextLevel = this.getLevel(guild, key) + 1;
        Optional<UpgradeLevelDefinition> nextLevelDefinition = definitionOption.get().getLevel(nextLevel);
        if (nextLevelDefinition.isEmpty()) {
            return PurchaseResult.MAX_LEVEL;
        }

        UpgradeLevelDefinition levelDefinition = nextLevelDefinition.get();

        // check if player can afford the upgrade
        if (this.getEconomyType() == GuildUpgradesConfiguration.EconomyType.VAULT) {
            if (!VaultHook.isEconomyHooked() || !VaultHook.canAfford(player, levelDefinition.vaultCost)) {
                return PurchaseResult.CANNOT_AFFORD;
            }
        } else {
            Option<ItemStack> requiredItem = this.upgradeItemStore.getRequiredItem(key, nextLevel);
            if (requiredItem.isEmpty()) {
                return PurchaseResult.CANNOT_AFFORD;
            }

            ItemStack required = requiredItem.get();
            if (!player.getInventory().containsAtLeast(required, required.getAmount())) {
                return PurchaseResult.CANNOT_AFFORD;
            }
        }

        if (!SimpleEventHandler.handle(new GuildUpgradePurchaseEvent(EventCause.USER, user, guild, key, nextLevel))) {
            return PurchaseResult.CANCELLED;
        }

        // take money/item from the player
        if (this.getEconomyType() == GuildUpgradesConfiguration.EconomyType.VAULT) {
            VaultHook.withdrawFromPlayerBank(player, levelDefinition.vaultCost);
        } else {
            this.upgradeItemStore.getRequiredItem(key, nextLevel)
                    .peek(required -> player.getInventory().removeItem(required));
        }

        guild.setUpgradeLevel(key, nextLevel);
        return PurchaseResult.SUCCESS;
    }

    public enum PurchaseResult {
        SUCCESS,
        DISABLED,
        UNKNOWN_UPGRADE,
        MAX_LEVEL,
        CANNOT_AFFORD,
        CANCELLED
    }

}
