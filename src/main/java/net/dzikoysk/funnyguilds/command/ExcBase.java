package net.dzikoysk.funnyguilds.command;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.LocationUtils;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class ExcBase implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfig c = Settings.getConfig();
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) sender;
        User user = User.get(p);

        if (!c.baseEnable) {
            p.sendMessage(m.baseTeleportationDisabled);
            return;
        }

        if (!user.hasGuild()) {
            p.sendMessage(m.baseHasNotGuild);
            return;
        }

        Guild guild = user.getGuild();

        if (user.getTeleportation() != null) {
            p.sendMessage(m.baseIsTeleportation);
            return;
        }

        ItemStack[] items = Settings.getConfig().baseItems.toArray(new ItemStack[0]);
        
        for (ItemStack is : items) {
            if (!p.getInventory().containsAtLeast(is, is.getAmount())) {
                String msg = m.baseItems;
                if (msg.contains("{ITEM}")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(is.getAmount());
                    sb.append(" ");
                    sb.append(is.getType().toString().toLowerCase());
                    msg = msg.replace("{ITEM}", sb.toString());
                }
                
                if (msg.contains("{ITEMS}")) {
                    ArrayList<String> list = new ArrayList<String>();
                    for (ItemStack it : items) {
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

        int time = Settings.getConfig().baseDelay;
        if (time < 1) {
            p.teleport(guild.getHome());
            p.sendMessage(m.baseTeleport);
            return;
        }

        p.sendMessage(m.baseDontMove.replace("{TIME}", Integer.toString(time)));

        Location before = p.getLocation();
        AtomicInteger i = new AtomicInteger(1);

        user.setTeleportation(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), () -> {
            if (!p.isOnline()) {
                user.getTeleportation().cancel();
                user.setTeleportation(null);
                return;
            }
            
            if (!LocationUtils.equals(p.getLocation(), before)) {
                user.getTeleportation().cancel();
                p.sendMessage(m.baseMove);
                user.setTeleportation(null);
                p.getInventory().addItem(items);
                return;
            }

            if (i.getAndIncrement() > time) {
                user.getTeleportation().cancel();
                p.sendMessage(m.baseTeleport);
                p.teleport(guild.getHome());
                user.setTeleportation(null);
            }
        }, 0L, 20L));
    }
}
