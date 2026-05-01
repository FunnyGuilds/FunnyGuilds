package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Locale;
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
        String setName = this.guildItemSetService.getNameForSet(set);

        if (this.itemsConfiguration.gui.enabled) {
            GuildItemsGui gui = GuildItemsGuiFactory.create(
                    player, user, set, setName,
                    this.itemsConfiguration, this.messageService
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
                        .with("{STATUS_COLOR}", result.isMeetsMoney() ? "<green>" : "<red>")
                        .with("{CURRENT}", String.format(Locale.ROOT, "%.0f", current))
                        .with("{REQUIRED}", String.format(Locale.ROOT, "%.0f", set.requiredMoney))
                        .send();
            }
            if (set.requirements.levelEnabled) {
                this.messageService.getMessage(config -> config.itemsRequirementLevel)
                        .receiver(player)
                        .with("{STATUS_COLOR}", result.isMeetsLevel() ? "<green>" : "<red>")
                        .with("{CURRENT}", player.getLevel())
                        .with("{REQUIRED}", set.requiredLevel)
                        .send();
            }
            if (set.requirements.rankEnabled) {
                this.messageService.getMessage(config -> config.itemsRequirementRank)
                        .receiver(player)
                        .with("{STATUS_COLOR}", result.isMeetsRank() ? "<green>" : "<red>")
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
                            .with("{STATUS_COLOR}", counts.isMet() ? "<green>" : "<red>")
                            .with("{ITEM}", counts.getDisplayName())
                            .with("{KEY}", entry.getKey())
                            .with("{CURRENT}", counts.getInv())
                            .with("{REQUIRED}", counts.getRequired())
                            .send();
                }
            }
        }
    }

}
