package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import net.dzikoysk.funnyguilds.command.IsMember;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

@FunnyComponent
public final class BaseCommand {

    @FunnyCommand(
        name = "${user.base.name}",
        aliases = "${user.base.aliases}",
        description = "${user.base.description}",
        permission = "funnyguilds.base",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, @IsMember User user, Guild guild) {
        when(!config.regionsEnabled, messages.regionsDisabled);
        when(!config.baseEnable, messages.baseTeleportationDisabled);
        when(user.getCache().getTeleportation() != null, messages.baseIsTeleportation);

        List<ItemStack> requiredItems = player.hasPermission("funnyguilds.vip.base")
                ? Collections.emptyList()
                : config.baseItems;

        if (!ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return;
        }

        ItemStack[] items = ItemUtils.toArray(requiredItems);
        player.getInventory().removeItem(items);

        if (config.baseDelay < 1) {
            player.teleport(guild.getHome());
            player.sendMessage(messages.baseTeleport);
            return;
        }

        int time = player.hasPermission("funnyguilds.vip.baseTeleportTime")
                ? config.baseDelayVip
                : config.baseDelay;

        Location before = player.getLocation();
        AtomicInteger timeCounter = new AtomicInteger(1);
        UserCache cache = user.getCache();

        cache.setTeleportation(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), () -> {
            if (!player.isOnline()) {
                cache.getTeleportation().cancel();
                cache.setTeleportation(null);
                return;
            }
            
            if (!LocationUtils.equals(player.getLocation(), before)) {
                cache.getTeleportation().cancel();
                player.sendMessage(messages.baseMove);
                cache.setTeleportation(null);
                player.getInventory().addItem(items);
                return;
            }

            if (timeCounter.getAndIncrement() > time) {
                cache.getTeleportation().cancel();
                player.sendMessage(messages.baseTeleport);
                player.teleport(guild.getHome());
                cache.setTeleportation(null);
            }
        }, 0L, 20L));

        player.sendMessage(messages.baseDontMove.replace("{TIME}", Integer.toString(time)));
    }

}
