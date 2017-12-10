package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.ItemUtils;
import net.dzikoysk.funnyguilds.util.TimeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.List;

public class ExcValidity implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);
        Guild guild = user.getGuild();

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        if (config.validityWhen != 0) {
            long c = guild.getValidity();
            long d = c - System.currentTimeMillis();
            
            if (d > config.validityWhen) {
                long when = d - config.validityWhen;
                player.sendMessage(messages.validityWhen.replace("{TIME}", TimeUtils.getDurationBreakdown(when)));
                return;
            }
        }

        List<ItemStack> requiredItems = config.validityItems;
        for (ItemStack requiredItem : requiredItems) {
            if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                continue;
            }

            String msg = ItemUtils.translatePlaceholder(messages.validityItems, requiredItems, requiredItem);
            player.sendMessage(msg);
            return;
        }
        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

        long c = guild.getValidity();
        if (c == 0) {
            c = System.currentTimeMillis();
        }

        c += config.validityTime;
        guild.setValidity(c);

        player.sendMessage(messages.validityDone.replace("{DATE}", config.dateFormat.format(new Date(c))));
    }

}
