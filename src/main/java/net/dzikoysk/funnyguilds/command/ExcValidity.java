package net.dzikoysk.funnyguilds.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.TimeUtils;

public class ExcValidity implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfig pc = Settings.getConfig();
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) sender;
        User user = User.get(p);
        Guild guild = user.getGuild();

        if (!user.hasGuild()) {
            p.sendMessage(m.validityHasNotGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            p.sendMessage(m.validityIsNotOwner);
            return;
        }

        if (pc.validityWhen != 0) {
            long c = guild.getValidity();
            long d = c - System.currentTimeMillis();
            
            if (d > pc.validityWhen) {
                long when = d - pc.validityWhen;
                p.sendMessage(m.validityWhen.replace("{TIME}", TimeUtils.getDurationBreakdown(when)));
                return;
            }
        }

        List<ItemStack> itemsList = pc.validityItems;
        for (ItemStack itemStack : itemsList) {
            if (!p.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                String msg = m.validityItems;
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
                
                p.sendMessage(msg);
                return;
            }
            
            p.getInventory().removeItem(itemStack);
        }

        long c = guild.getValidity();
        if (c == 0) {
            c = System.currentTimeMillis();
        }

        c += pc.validityTime;
        guild.setValidity(c);

        p.sendMessage(m.validityDone.replace("{DATE}", pc.dateFormat.format(new Date(c))));
    }
}
