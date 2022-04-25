package net.dzikoysk.funnyguilds.feature.command.admin;

import java.util.List;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildMoveEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class MoveCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.move.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, User admin, String[] args) {
        when(!config.regionsEnabled, messages.regionsDisabled);
        when(args.length < 1, messages.generalNoTagGiven);

        HeartConfiguration heartConfig = config.heart;
        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        Location location = player.getLocation().getBlock().getLocation();
        World world = player.getWorld();

        if (!heartConfig.usePlayerPositionForCenterY) {
            location.setY(heartConfig.createCenterY);
        }

        if (heartConfig.createEntityType != null && location.getBlockY() < (world.getMaxHeight() - 2)) {
            location.setY(location.getBlockY() + 2);
        }

        int distance = config.regionSize + config.createDistance;

        if (config.enlargeItems != null) {
            distance = config.enlargeItems.size() * config.enlargeSize + distance;
        }

        when(distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), location),
                messages.createSpawn.replace("{DISTANCE}", Integer.toString(distance)));
        when(this.regionManager.isNearRegion(location), messages.createIsNear);

        if (!SimpleEventHandler.handle(new GuildMoveEvent(AdminUtils.getCause(admin), admin, guild, location))) {
            return;
        }

        Region region = guild.getRegion()
                .peek(peekRegion -> {
                    if (heartConfig.createEntityType != null) {
                        plugin.getGuildEntityHelper().despawnGuildEntity(guild);
                    }
                    else if (heartConfig.createMaterial != null && heartConfig.createMaterial.getLeft() != Material.AIR) {
                        peekRegion.getHeartBlock()
                                .filter(heart -> heart.getLocation().getBlockY() > 1)
                                .peek(heart -> Bukkit.getScheduler().runTask(this.plugin, () -> heart.setType(Material.AIR)));
                    }

                    peekRegion.setCenter(location);
                    guild.getEnderCrystal()
                            .map(Location::clone)
                            .map(homeLocation -> homeLocation.subtract(0.0D, 1.0D, 0.0D))
                            .peek(guild::setHome);
                })
                .orElseGet(() -> new Region(guild, location, config.regionSize));

        if (heartConfig.createCenterSphere) {
            List<Location> sphere = SpaceUtils.sphere(location, 3, 3, false, true, 0);

            for (Location locationInSphere : sphere) {
                if (locationInSphere.getBlock().getType() != Material.BEDROCK) {
                    locationInSphere.getBlock().setType(Material.AIR);
                }
            }
        }

        plugin.getGuildEntityHelper().spawnGuildEntity(guild);
        admin.sendMessage(messages.adminGuildRelocated.replace("{GUILD}", guild.getName()).replace("{REGION}", region.getName()));
    }

}
