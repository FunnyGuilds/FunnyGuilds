package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Map;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult.ItemCountResult;
import net.dzikoysk.funnyguilds.feature.items.gui.GuildItemsGui;
import net.dzikoysk.funnyguilds.feature.items.gui.GuildItemsGuiFactory;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

@FunnyComponent
public final class ItemsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.items.name}",
            description = "${user.items.description}",
            aliases = "${user.items.aliases}",
            permission = "funnyguilds.items",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, User user) {
        GuildItemSet set = this.guildItemSetService.getSetForPlayer(player);

        String setName = this.itemsConfiguration.getGuildItemSets().entrySet().stream()
                .filter(e -> e.getValue() == set)
                .map(Map.Entry::getKey)
                .findFirst().orElse("default");

        if (this.itemsConfiguration.gui.enabled) {
            GuildItemsGui gui = GuildItemsGuiFactory.create(
                    player, user, set, setName,
                    this.itemsConfiguration, this.messageService, this.plugin
            );
            gui.open();
        } else {
            ItemRequirementResult result = this.guildItemRequirementChecker.check(player, user, set, this.itemsConfiguration);

            this.messageService.getMessage(config -> config.itemsCheckHeader)
                    .receiver(player)
                    .with("{PLAYER}", player.getName())
                    .with("{SET}", setName)
                    .send();

            if (set.requirements.moneyEnabled) {
                double current = VaultHook.isEconomyHooked() ? VaultHook.accountBalance(player) : 0;
                this.messageService.getMessage(config -> config.itemsRequirementMoney)
                        .receiver(player)
                        .with("{CURRENT}", String.format("%.0f", current))
                        .with("{REQUIRED}", String.format("%.0f", set.requiredMoney))
                        .send();
            }
            if (set.requirements.experienceEnabled) {
                this.messageService.getMessage(config -> config.itemsRequirementExperience)
                        .receiver(player)
                        .with("{CURRENT}", player.getLevel())
                        .with("{REQUIRED}", set.requiredExperience)
                        .send();
            }
            if (set.requirements.rankEnabled) {
                this.messageService.getMessage(config -> config.itemsRequirementRank)
                        .receiver(player)
                        .with("{CURRENT}", user.getRank().getPoints())
                        .with("{REQUIRED}", set.requiredRank)
                        .send();
            }
            if (set.requirements.itemsEnabled) {
                this.messageService.getMessage(config -> config.itemsRequirementItemsHeader)
                        .receiver(player)
                        .send();
                for (Map.Entry<String, ItemCountResult> entry : result.getItemCounts().entrySet()) {
                    ItemCountResult counts = entry.getValue();
                    this.messageService.getMessage(config -> config.itemsRequirementItemLine)
                            .receiver(player)
                            .with("{ITEM}", counts.getDisplayName())
                            .with("{KEY}", entry.getKey())
                            .with("{CURRENT}", counts.getTotal())
                            .with("{REQUIRED}", counts.getRequired())
                            .send();
                }
            }
        }
    }

}