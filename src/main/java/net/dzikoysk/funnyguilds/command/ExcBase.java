package net.dzikoysk.funnyguilds.command;

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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcBase implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!config.baseEnable) {
            player.sendMessage(messages.baseTeleportationDisabled);
            return;
        }

        if (!user.hasGuild()) {
            player.sendMessage(messages.baseHasNotGuild);
            return;
        }

        Guild guild = user.getGuild();

        if (user.getTeleportation() != null) {
            player.sendMessage(messages.baseIsTeleportation);
            return;
        }

        ItemStack[] items = Settings.getConfig().baseItems.toArray(new ItemStack[0]);
        
        for (ItemStack is : items) {
            if (!player.getInventory().containsAtLeast(is, is.getAmount())) {
                String msg = messages.baseItems;
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
                
                player.sendMessage(msg);
                return;
            }
        }
        
        player.getInventory().removeItem(items);

        int time = Settings.getConfig().baseDelay;
        if (time < 1) {
            player.teleport(guild.getHome());
            player.sendMessage(messages.baseTeleport);
            return;
        }

        player.sendMessage(messages.baseDontMove.replace("{TIME}", Integer.toString(time)));

        Location before = player.getLocation();
        AtomicInteger i = new AtomicInteger(1);

        user.setTeleportation(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), () -> {
            if (!player.isOnline()) {
                user.getTeleportation().cancel();
                user.setTeleportation(null);
                return;
            }
            
            if (!LocationUtils.equals(player.getLocation(), before)) {
                user.getTeleportation().cancel();
                player.sendMessage(messages.baseMove);
                user.setTeleportation(null);
                player.getInventory().addItem(items);
                return;
            }

            if (i.getAndIncrement() > time) {
                user.getTeleportation().cancel();
                player.sendMessage(messages.baseTeleport);
                player.teleport(guild.getHome());
                user.setTeleportation(null);
            }
        }, 0L, 20L));
    }

}
