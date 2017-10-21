package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.LocationUtils;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcBase implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        final MessagesConfig m = Messages.getInstance();
        final Player p = (Player) s;
        final User user = User.get(p);

        if (!user.hasGuild()) {
            p.sendMessage(m.baseHasNotGuild);
            return;
        }

        final Guild guild = user.getGuild();

        if (user.getTeleportation() != null) {
            p.sendMessage(m.baseIsTeleportation);
            return;
        }

        List<ItemStack> itemsList = Settings.getConfig().baseItems;
        final ItemStack[] items = itemsList.toArray(new ItemStack[0]);
        for (int i = 0; i < items.length; i++) {
            if (!p.getInventory().containsAtLeast(items[i], items[i].getAmount())) {
                String msg = m.baseItems;
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

        final int time = Settings.getConfig().baseDelay;
        if (time < 1) {
            p.teleport(guild.getHome());
            p.sendMessage(m.baseTeleport);
            return;
        }

        p.sendMessage(m.baseDontMove.replace("{TIME}", Integer.toString(time)));

        final Location before = p.getLocation();
        final AtomicInteger i = new AtomicInteger(0);

        user.setTeleportation(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), () -> {
            i.getAndIncrement();

            if (!LocationUtils.equals(p.getLocation(), before)) {
                user.getTeleportation().cancel();
                p.sendMessage(m.baseMove);
                user.setTeleportation(null);
                p.getInventory().addItem(items);
                return;
            }

            if (i.get() > time) {
                user.getTeleportation().cancel();
                p.sendMessage(m.baseTeleport);
                p.teleport(guild.getHome());
                user.setTeleportation(null);
            }
        }, 0L, 20L));
    }

}
