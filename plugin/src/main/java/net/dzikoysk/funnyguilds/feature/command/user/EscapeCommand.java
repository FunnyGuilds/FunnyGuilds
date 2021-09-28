package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.whenNull;

@FunnyComponent
public final class EscapeCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${user.escape.name}",
        description = "${user.escape.description}",
        aliases = "${user.escape.aliases}",
        permission = "funnyguilds.escape",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(Player player, User user) {
        when (!pluginConfiguration.regionsEnabled, messageConfiguration.regionsDisabled);
        when (!pluginConfiguration.escapeEnable || !pluginConfiguration.baseEnable, messageConfiguration.escapeDisabled);
        when (user.getCache().getTeleportation() != null, messageConfiguration.escapeInProgress);

        Location playerLocation = player.getLocation();
        Region region = RegionUtils.getAt(playerLocation);
        whenNull (region, messageConfiguration.escapeNoNeedToRun);

        int time = this.pluginConfiguration.escapeDelay;

        if (!user.hasGuild()) {
            when (!pluginConfiguration.escapeSpawn, messageConfiguration.escapeNoUserGuild);
            scheduleTeleportation(player, user, player.getWorld().getSpawnLocation(), time, () -> {});
            return;
        }
        
        Guild guild = user.getGuild();
        when (guild.equals(region.getGuild()), messageConfiguration.escapeOnYourRegion);

        if (time >= 1) {
            player.sendMessage(messageConfiguration.escapeStartedUser.replace("{TIME}", Integer.toString(time)));

            String msg = messageConfiguration.escapeStartedOpponents.replace("{TIME}", Integer.toString(time)).replace("{PLAYER}", player.getName())
                    .replace("{X}", Integer.toString(playerLocation.getBlockX())).replace("{Y}", Integer.toString(playerLocation.getBlockY()))
                    .replace("{Z}", Integer.toString(playerLocation.getBlockZ()));

            for (User member : region.getGuild().getOnlineMembers()) {
                member.getPlayer().sendMessage(msg);
            }
        }
        
        scheduleTeleportation(player, user, guild.getHome(), time, () -> {
            for (User member : region.getGuild().getOnlineMembers()) {
                member.getPlayer().sendMessage(messageConfiguration.escapeSuccessfulOpponents.replace("{PLAYER}", player.getName()));
            }
        });
    }

    private void scheduleTeleportation(Player player, User user, Location destination, int time, Runnable onSuccess) {
        Location before = player.getLocation();
        AtomicInteger timeCounter = new AtomicInteger(0);
        UserCache cache = user.getCache();

        cache.setTeleportation(Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (!player.isOnline()) {
                cache.getTeleportation().cancel();
                cache.setTeleportation(null);
                return;
            }

            if (!LocationUtils.equals(player.getLocation(), before)) {
                cache.getTeleportation().cancel();
                player.sendMessage(this.messageConfiguration.escapeCancelled);
                cache.setTeleportation(null);
                return;
            }

            if (timeCounter.getAndIncrement() > time) {
                cache.getTeleportation().cancel();
                player.teleport(destination);
                player.sendMessage(this.messageConfiguration.escapeSuccessfulUser);
                onSuccess.run();
                cache.setTeleportation(null);
            }
        }, 0L, (time < 1) ? 0L : 20L));
    }

}