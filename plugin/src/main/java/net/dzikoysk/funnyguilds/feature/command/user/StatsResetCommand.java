package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.List;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
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
        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, this.messages.statsResetItems)) {
            return;
        }
        int lastRank = user.getRank().getPoints();
        int lastDeaths = user.getRank().getDeaths();
        int lastKills = user.getRank().getKills();
        user.getRank().setPoints(this.config.rankStart);
        user.getRank().setDeaths(0);
        user.getRank().setKills(0);
        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{LAST-RANK}", lastRank)
                .register("{CURRENT-RANK}", user.getRank().getPoints())
                .register("{LAST-DEATH}", lastDeaths)
                .register("{CURRENT-DEATH", user.getRank().getDeaths())
                .register("{LAST-KILLS}", lastKills)
                .register("{CURRENT-KILLS}", user.getRank().getKills());

        user.sendMessage(formatter.format(this.messages.statsResetMessage));
    }

}
