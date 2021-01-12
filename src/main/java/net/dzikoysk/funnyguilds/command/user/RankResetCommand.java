package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class RankResetCommand {

    @FunnyCommand(
        name = "${user.rank-reset.name}",
        description = "${user.rank-reset.description}",
        aliases = "${user.rank-reset.aliases}",
        permission = "funnyguilds.rankreset",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, User user) {
        List<ItemStack> requiredItems = config.rankResetItems;

        if (! ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return;
        }

        if (user != null) {
            int lastRank = user.getRank().getPoints();
            user.getRank().setPoints(config.rankStart);
            player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

            String resetMessage = messages.rankResetMessage;
            resetMessage = StringUtils.replace(resetMessage, "{LAST-RANK}", String.valueOf(lastRank));
            resetMessage = StringUtils.replace(resetMessage, "{CURRENT-RANK}", String.valueOf(user.getRank().getPoints()));

            player.sendMessage(resetMessage);
        }
    }

}
