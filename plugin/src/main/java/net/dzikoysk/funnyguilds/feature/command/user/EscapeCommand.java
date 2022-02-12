package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.concurrent.atomic.AtomicInteger;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
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
        when(!config.regionsEnabled, messages.regionsDisabled);
        when(!config.escapeEnable || !config.baseEnable, messages.escapeDisabled);
        when(user.getCache().getTeleportation() != null, messages.escapeInProgress);

        Location playerLocation = player.getLocation();
        Option<Region> regionOption = this.regionManager.findRegionAtLocation(playerLocation);
        when(regionOption.isEmpty(), messages.escapeNoNeedToRun);
        Region region = regionOption.get();

        int time = config.escapeDelay;

        if (!user.hasGuild()) {
            when(!config.escapeSpawn, messages.escapeNoUserGuild);
            scheduleTeleportation(player, user, player.getWorld().getSpawnLocation(), time, () -> {
            });
            return;
        }

        Guild guild = user.getGuildOption().get();
        when(guild.equals(region.getGuildOption().getOrNull()), messages.escapeOnYourRegion);

        if (time >= 1) {
            player.sendMessage(messages.escapeStartedUser.replace("{TIME}", Integer.toString(time)));

            String msg = messages.escapeStartedOpponents.replace("{TIME}", Integer.toString(time)).replace("{PLAYER}", player.getName())
                    .replace("{X}", Integer.toString(playerLocation.getBlockX())).replace("{Y}", Integer.toString(playerLocation.getBlockY()))
                    .replace("{Z}", Integer.toString(playerLocation.getBlockZ()));

            region.getGuildOption()
                    .map(Guild::getOnlineMembers)
                    .peek(members ->
                            members.forEach(member -> member.sendMessage(msg)));
        }

        guild.getHomeOption().peek(home -> scheduleTeleportation(player, user, home, time, () ->
                region.getGuildOption()
                        .map(Guild::getOnlineMembers)
                        .peek(members -> members.forEach(member ->
                                member.sendMessage(messages.escapeSuccessfulOpponents.replace("{PLAYER}", player.getName()))))));
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