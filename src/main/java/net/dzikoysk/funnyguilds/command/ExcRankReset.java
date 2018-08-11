package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.util.commons.spigot.ItemComponentUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ExcRankReset implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        List<ItemStack> requiredItems = config.rankResetItems;

        for (ItemStack requiredItem : requiredItems) {
            if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                continue;
            }

            if (config.enableItemComponent) {
                player.spigot().sendMessage(ItemComponentUtils.translateComponentPlaceholder(messages.createItems, requiredItems, requiredItem));
            } else {
                player.sendMessage(ItemUtils.translateTextPlaceholder(messages.createItems, requiredItems, requiredItem));
            }
            
            return;
        }

        User user = User.get(player);

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
