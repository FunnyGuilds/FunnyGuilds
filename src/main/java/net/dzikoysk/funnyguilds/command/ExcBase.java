package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.ItemUtils;
import net.dzikoysk.funnyguilds.util.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
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

        Collection<ItemStack> requiredItems = config.baseItems;

        for (ItemStack requiredItem : requiredItems) {
            if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                continue;
            }

            String msg = ItemUtils.translatePlaceholder(messages.baseItems, requiredItems, requiredItem);
            player.sendMessage(msg);
            return;
        }

        ItemStack[] items = ItemUtils.toArray(requiredItems);
        player.getInventory().removeItem(items);

        if (config.baseDelay < 1) {
            player.teleport(guild.getHome());
            player.sendMessage(messages.baseTeleport);
            return;
        }

        int time = config.baseDelay;
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

        player.sendMessage(messages.baseDontMove.replace("{TIME}", Integer.toString(time)));
    }

}
