package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.TimeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcValidity implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfig s = Settings.getConfig();
        Messages m = Messages.getInstance();
        Player p = (Player) sender;
        User user = User.get(p);
        Guild guild = user.getGuild();

        if (!user.hasGuild()) {
            p.sendMessage(m.getMessage("validityHasNotGuild"));
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            p.sendMessage(m.getMessage("validityIsNotOwner"));
            return;
        }

        if (s.validityWhen != 0) {
            long c = guild.getValidity();
            long d = c - System.currentTimeMillis();
            if (d > s.validityWhen) {
                long when = d - s.validityWhen;
                p.sendMessage(m.getMessage("validityWhen")
                        .replace("{TIME}", TimeUtils.getDurationBreakdown(when))
                );
                return;
            }
        }

        List<ItemStack> itemsList = s.validityItems;
        ItemStack[] items = itemsList.toArray(new ItemStack[0]);
        for (int i = 0; i < items.length; i++) {
            if (!p.getInventory().containsAtLeast(items[i], items[i].getAmount())) {
                String msg = m.getMessage("validityItems");
                if (msg.contains("{ITEM}")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(items[i].getAmount());
                    sb.append(" ");
                    sb.append(items[i].getType().toString().toLowerCase());
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
        }
        p.getInventory().removeItem(items);

        long c = guild.getValidity();
        if (c == 0) {
            c = System.currentTimeMillis();
        }
        c += Settings.getConfig().validityTime;
        guild.setValidity(c);

        DateFormat date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date v = new Date(c);

        p.sendMessage(m.getMessage("validityDone")
                .replace("{DATE}", date.format(v))
        );
    }

}
