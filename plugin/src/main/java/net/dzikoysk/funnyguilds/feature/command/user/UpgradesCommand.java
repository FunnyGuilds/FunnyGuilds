package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.sections.upgrades.UpgradeDefinition;
import net.dzikoysk.funnyguilds.config.sections.upgrades.UpgradeLevelDefinition;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.feature.upgrades.GuildUpgradeService.PurchaseResult;
import net.dzikoysk.funnyguilds.feature.upgrades.gui.GuildUpgradesGui;
import net.dzikoysk.funnyguilds.feature.upgrades.gui.GuildUpgradesGuiFactory;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class UpgradesCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.upgrades.name}",
            description = "${user.upgrades.description}",
            aliases = "${user.upgrades.aliases}",
            permission = "funnyguilds.upgrades",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @HasGuildPermission(GuildCommandPermission.UPGRADES) User deputy, Guild guild, String[] args) {
        when(!this.config.guildUpgrades.enabled, config -> config.upgradesDisabled);

        if (args.length >= 1) {
            String key = args[0].toLowerCase(Locale.ROOT);
            PurchaseResult result = this.guildUpgradeService.purchase(player, deputy, guild, key);
            this.sendPurchaseResult(player, guild, key, result);
            return;
        }

        if (this.upgradesMenuConfiguration.enabled) {
            GuildUpgradesGui gui = GuildUpgradesGuiFactory.create(
                    player, deputy, guild, this.upgradesMenuConfiguration, this.guildUpgradeService, this.messageService
            );
            gui.open();
            return;
        }

        this.messageService.getMessage(config -> config.upgradesListHeader)
                .receiver(player)
                .send();

        for (Map.Entry<String, UpgradeDefinition> entry : this.config.guildUpgrades.getUpgrades().entrySet()) {
            String key = entry.getKey();

            int level = this.guildUpgradeService.getLevel(guild, key);
            int maxLevel = this.guildUpgradeService.getMaxLevel(key);
            double currentValue = this.guildUpgradeService.getCurrentLevelDefinition(guild, key)
                    .map(levelDefinition -> levelDefinition.value)
                    .orElse(0.0);
            Optional<UpgradeLevelDefinition> nextLevelDefinition = this.guildUpgradeService.getNextLevelDefinition(guild, key);
            double nextValue = nextLevelDefinition.map(levelDefinition -> levelDefinition.value).orElse(currentValue);
            String cost = nextLevelDefinition.map(levelDefinition -> String.valueOf(levelDefinition.vaultCost)).orElse("-");

            this.messageService.getMessage(config -> config.upgradesListLine)
                    .receiver(player)
                    .with("{UPGRADE}", key)
                    .with("{LEVEL}", level)
                    .with("{MAX_LEVEL}", maxLevel)
                    .with("{VALUE}", currentValue)
                    .with("{NEXT_VALUE}", nextValue)
                    .with("{COST}", cost)
                    .send();
        }
    }

    private void sendPurchaseResult(Player player, Guild guild, String key, PurchaseResult result) {
        switch (result) {
            case SUCCESS -> {
                int newLevel = this.guildUpgradeService.getLevel(guild, key);
                this.messageService.getMessage(config -> config.upgradesPurchased)
                        .receiver(guild)
                        .with("{PLAYER}", player.getName())
                        .with("{UPGRADE}", key)
                        .with("{LEVEL}", newLevel)
                        .send();
            }
            case MAX_LEVEL -> this.messageService.getMessage(config -> config.upgradesMaxLevel)
                    .receiver(player)
                    .send();
            case CANNOT_AFFORD -> this.messageService.getMessage(config -> config.upgradesCannotAfford)
                    .receiver(player)
                    .send();
            case DISABLED -> this.messageService.getMessage(config -> config.upgradesDisabled)
                    .receiver(player)
                    .send();
            case UNKNOWN_UPGRADE -> this.messageService.getMessage(config -> config.upgradesUnknownUpgrade)
                    .receiver(player)
                    .with("{UPGRADE}", key)
                    .send();
            case CANCELLED -> {
            }
        }
    }

}
