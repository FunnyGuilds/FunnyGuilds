package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.List;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserStatKeys;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunnyComponent
public final class StatsResetCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.stats-reset.name}",
            description = "${user.stats-reset.description}",
            aliases = "${user.stats-reset.aliases}",
            permission = "funnyguilds.statsreset",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, User user) {
        List<ItemStack> requiredItems = this.config.statsResetItems;
        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, config -> config.statsResetItems)) {
            return;
        }

        int lastPoints = this.rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0);
        int lastKills = this.rankService.getUserStatValue(user, UserStatKeys.KILLS).orElse(0);
        int lastDeaths = this.rankService.getUserStatValue(user, UserStatKeys.DEATHS).orElse(0);
        int lastAssists = this.rankService.getUserStatValue(user, UserStatKeys.ASSISTS).orElse(0);
        int lastLogouts = this.rankService.getUserStatValue(user, UserStatKeys.LOGOUTS).orElse(0);

        this.rankService.setUserStatValue(user, UserStatKeys.POINTS, Math.max(0, this.config.rankStart));
        this.rankService.setUserStatValue(user, UserStatKeys.KILLS, 0);
        this.rankService.setUserStatValue(user, UserStatKeys.DEATHS, 0);
        this.rankService.setUserStatValue(user, UserStatKeys.ASSISTS, 0);
        this.rankService.setUserStatValue(user, UserStatKeys.LOGOUTS, 0);

        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{LAST-POINTS}", lastPoints)
                .register("{CURRENT-POINTS}", this.rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0))
                .register("{LAST-DEATHS}", lastDeaths)
                .register("{CURRENT-DEATHS}", this.rankService.getUserStatValue(user, UserStatKeys.DEATHS).orElse(0))
                .register("{LAST-KILLS}", lastKills)
                .register("{CURRENT-KILLS}", this.rankService.getUserStatValue(user, UserStatKeys.KILLS).orElse(0))
                .register("{LAST-ASSISTS}", lastAssists)
                .register("{CURRENT-ASSISTS}", this.rankService.getUserStatValue(user, UserStatKeys.ASSISTS).orElse(0))
                .register("{LAST-LOGOUTS}", lastLogouts)
                .register("{CURRENT-LOGOUTS}", this.rankService.getUserStatValue(user, UserStatKeys.LOGOUTS).orElse(0));

        this.messageService.getMessage(config -> config.statsResetMessage)
                .receiver(player)
                .with(formatter)
                .send();
    }

}
