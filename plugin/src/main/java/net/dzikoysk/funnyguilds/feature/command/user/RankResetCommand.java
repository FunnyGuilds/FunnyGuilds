package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@FunnyComponent
public final class RankResetCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${user.rank-reset.name}",
        description = "${user.rank-reset.description}",
        aliases = "${user.rank-reset.aliases}",
        permission = "funnyguilds.rankreset",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(Player player, User user) {
        List<ItemStack> requiredItems = config.rankResetItems;

        if (!ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return;
        }

        int lastRank = user.getRank().getPoints();
        user.getRank().setPoints(config.rankStart);
        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

        String resetMessage = messages.rankResetMessage;
        resetMessage = StringUtils.replace(resetMessage, "{LAST-RANK}", String.valueOf(lastRank));
        resetMessage = StringUtils.replace(resetMessage, "{CURRENT-RANK}", String.valueOf(user.getRank().getPoints()));

        player.sendMessage(resetMessage);
    }

}
