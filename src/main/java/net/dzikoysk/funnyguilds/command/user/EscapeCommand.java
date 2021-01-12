package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

@FunnyComponent
public final class EscapeCommand {

    @FunnyCommand(
        name = "${user.escape.name}",
        description = "${user.escape.description}",
        aliases = "${user.escape.aliases}",
        permission = "funnyguilds.escape",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, User user) {
        if (!config.regionsEnabled) {
            player.sendMessage(messages.regionsDisabled);
            return;
        }
        
        if (!config.escapeEnable || !config.baseEnable) {
            player.sendMessage(messages.escapeDisabled);
            return;
        }

        if (user.getCache().getTeleportation() != null) {
            player.sendMessage(messages.escapeInProgress);
            return;
        }

        Location playerLocation = player.getLocation();
        Region region = RegionUtils.getAt(playerLocation);

        if (region == null) {
            player.sendMessage(messages.escapeNoNeedToRun);
            return;
        }

        int time = FunnyGuilds.getInstance().getPluginConfiguration().escapeDelay;

        if (!user.hasGuild()) {
            if (config.escapeSpawn) {
                scheduleTeleportation(player, user, player.getWorld().getSpawnLocation(), time, () -> {});
                return;
            }

            player.sendMessage(messages.escapeNoUserGuild);
            return;
        }
        
        Guild guild = user.getGuild();

        if (guild.equals(region.getGuild())) {
            player.sendMessage(messages.escapeOnYourRegion);
            return;
        }

        if (time >= 1) {
            player.sendMessage(messages.escapeStartedUser.replace("{TIME}", Integer.toString(time)));

            String msg = messages.escapeStartedOpponents.replace("{TIME}", Integer.toString(time)).replace("{PLAYER}", player.getName())
                    .replace("{X}", Integer.toString(playerLocation.getBlockX())).replace("{Y}", Integer.toString(playerLocation.getBlockY()))
                    .replace("{Z}", Integer.toString(playerLocation.getBlockZ()));

            for (User member : region.getGuild().getOnlineMembers()) {
                member.getPlayer().sendMessage(msg);
            }
        }
        
        scheduleTeleportation(player, user, guild.getHome(), time, () -> {
            for (User member : region.getGuild().getOnlineMembers()) {
                member.getPlayer().sendMessage(messages.escapeSuccessfulOpponents.replace("{PLAYER}", player.getName()));
            }
        });
    }

    private void scheduleTeleportation(Player player, User user, Location destination, int time, Runnable onSuccess) {
        Location before = player.getLocation();
        AtomicInteger timeCounter = new AtomicInteger(0);
        UserCache cache = user.getCache();

        cache.setTeleportation(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), () -> {
            if (!player.isOnline()) {
                cache.getTeleportation().cancel();
                cache.setTeleportation(null);
                return;
            }

            MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

            if (!LocationUtils.equals(player.getLocation(), before)) {
                cache.getTeleportation().cancel();
                player.sendMessage(messages.escapeCancelled);
                cache.setTeleportation(null);
                return;
            }


            if (timeCounter.getAndIncrement() > time) {
                cache.getTeleportation().cancel();
                player.teleport(destination);
                player.sendMessage(messages.escapeSuccessfulUser);
                onSuccess.run();
                cache.setTeleportation(null);
            }
        }, 0L, (time < 1) ? 0L : 20L));
    }

}