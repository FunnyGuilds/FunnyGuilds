package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.TimeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
            player.sendMessage(messages.validityHasNotGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.validityIsNotOwner);
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

        List<ItemStack> itemsList = config.validityItems;
        for (ItemStack itemStack : itemsList) {
            if (!player.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                String msg = messages.validityItems;
                if (msg.contains("{ITEM}")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(itemStack.getAmount());
                    sb.append(" ");
                    sb.append(itemStack.getType().toString().toLowerCase());
                    msg = msg.replace("{ITEM}", sb.toString());
                }
                
                if (msg.contains("{ITEMS}")) {
                    ArrayList<String> list = new ArrayList<String>();
                    for (ItemStack it : itemsList) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(it.getAmount());
                        sb.append(" ");
                        sb.append(it.getType().toString().toLowerCase());
                        list.add(sb.toString());
                    }
                    
                    msg = msg.replace("{ITEMS}", StringUtils.toString(list, true));
                }
                
                player.sendMessage(msg);
                return;
            }
            
            player.getInventory().removeItem(itemStack);
        }

        long c = guild.getValidity();
        if (c == 0) {
            c = System.currentTimeMillis();
        }

        c += config.validityTime;
        guild.setValidity(c);

        player.sendMessage(messages.validityDone.replace("{DATE}", config.dateFormat.format(new Date(c))));
    }

}
