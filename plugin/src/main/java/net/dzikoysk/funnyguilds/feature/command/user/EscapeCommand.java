package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.concurrent.atomic.AtomicInteger;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import panda.std.Option;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

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
        when(!this.config.regionsEnabled, this.messages.regionsDisabled);
        when(!this.config.escapeEnable || !this.config.baseEnable, this.messages.escapeDisabled);
        when(user.getCache().getTeleportation() != null, this.messages.escapeInProgress);

        Location playerLocation = player.getLocation();
        Option<Region> regionOption = this.regionManager.findRegionAtLocation(playerLocation);
        when(regionOption.isEmpty(), this.messages.escapeNoNeedToRun);

        Region region = regionOption.get();
        int time = this.config.escapeDelay;

        if (!user.hasGuild()) {
            when(!this.config.escapeSpawn, this.messages.escapeNoUserGuild);
            this.scheduleTeleportation(player, user, player.getWorld().getSpawnLocation(), time, () -> {});
            return;
        }

        Guild guild = user.getGuild().get();
        when(guild.equals(region.getGuild()), this.messages.escapeOnYourRegion);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{TIME}", time)
                .register("{PLAYER}", player.getName())
                .register("{X}", playerLocation.getBlockX())
                .register("{Y}", playerLocation.getBlockY())
                .register("{Z}", playerLocation.getBlockZ());

        if (time >= 1) {
            sendMessage(player, formatter.format(this.messages.escapeStartedUser));
            region.getGuild().broadcast(formatter.format(this.messages.escapeStartedOpponents));
        }

        guild.getHome().peek(home -> this.scheduleTeleportation(player, user, home, time, () -> {
            region.getGuild().getMembers().forEach(member -> {
                member.sendMessage(formatter.format(this.messages.escapeSuccessfulOpponents));
            });
        }));
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
                sendMessage(player, this.messages.escapeCancelled);
                cache.setTeleportation(null);
                return;
            }

            if (timeCounter.getAndIncrement() > time) {
                cache.getTeleportation().cancel();
                player.teleport(destination);
                sendMessage(player, this.messages.escapeSuccessfulUser);
                onSuccess.run();
                cache.setTeleportation(null);
            }
        }, 0L, (time < 1) ? 0L : 20L));
    }

}