package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.LocationUtils;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ExcBase implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {

        final Messages m = Messages.getInstance();
        final Player p = (Player) s;
        final User user = User.get(p);

        if (!user.hasGuild()) {
            p.sendMessage(m.getMessage("baseHasNotGuild"));
            return;
        }

        final Guild guild = user.getGuild();

        if (user.getTeleportation() != null) {
            p.sendMessage(m.getMessage("baseIsTeleportation"));
            return;
        }

        List<ItemStack> itemsList = Settings.getInstance().baseItems;
        final ItemStack[] items = itemsList.toArray(new ItemStack[0]);
        for (int i = 0; i < items.length; i++) {
            if (!p.getInventory().containsAtLeast(items[i], items[i].getAmount())) {
                String msg = m.getMessage("baseItems");
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

        final int time = Settings.getInstance().baseDelay;
        if (time < 1) {
            p.teleport(guild.getHome());
            p.sendMessage(m.getMessage("baseTeleport"));
            return;
        }

        p.sendMessage(m.getMessage("baseDontMove")
                        .replace("{TIME}", Integer.toString(time))
        );

        user.setTeleportation(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), new Runnable() {

            Location before = p.getLocation();
            Player player = p;
            int i = 0;

            @Override
            public void run() {
                i++;
                if (!LocationUtils.equals(player.getLocation(), before)) {
                    user.getTeleportation().cancel();
                    player.sendMessage(m.getMessage("baseMove"));
                    user.setTeleportation(null);
                    player.getInventory().addItem(items);
                    return;
                }
                if (i > time) {
                    user.getTeleportation().cancel();
                    player.sendMessage(m.getMessage("baseTeleport"));
                    player.teleport(guild.getHome());
                    user.setTeleportation(null);
                    return;
                }
            }
        }, 0, 20));
        return;
    }
}
