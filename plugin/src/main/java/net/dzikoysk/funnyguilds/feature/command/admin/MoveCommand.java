package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildMoveEvent;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.region.Region;
import net.dzikoysk.funnyguilds.guild.region.RegionUtils;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class MoveCommand {

    @FunnyCommand(
        name = "${admin.move.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(MessageConfiguration messages, PluginConfiguration config, Player player, String[] args) {
        when (!config.regionsEnabled, messages.regionsDisabled);
        when (args.length < 1, messages.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        Location location = player.getLocation();

        if (config.createCenterY != 0) {
            location.setY(config.createCenterY);
        }

        int distance = config.regionSize + config.createDistance;

        if (config.enlargeItems != null) {
            distance = config.enlargeItems.size() * config.enlargeSize + distance;
        }

        when (distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), location),
                messages.createSpawn.replace("{DISTANCE}", Integer.toString(distance)));
        when (RegionUtils.isNear(location), messages.createIsNear);

        User admin = AdminUtils.getAdminUser(player);
        if (!SimpleEventHandler.handle(new GuildMoveEvent(AdminUtils.getCause(admin), admin, guild, location))) {
            return;
        }
        
        Region region = guild.getRegion();

        if (region == null) {
            region = new Region(guild, location, config.regionSize);
        } else {
            if (config.createEntityType != null) {
                GuildEntityHelper.despawnGuildHeart(guild);
            } else if (config.createMaterial != null && config.createMaterial.getLeft() != Material.AIR) {
                Block block = region.getCenter().getBlock().getRelative(BlockFace.DOWN);
                
                Bukkit.getScheduler().runTask(FunnyGuilds.getInstance(), () -> {
                    if (block.getLocation().getBlockY() > 1) {
                        block.setType(Material.AIR);
                    }
                });
            }
            
            region.setCenter(location);
        }
        
        if (config.createCenterSphere) {
            List<Location> sphere = SpaceUtils.sphere(location, 3, 3, false, true, 0);

            for (Location locationInSphere : sphere) {
                if (locationInSphere.getBlock().getType() != Material.BEDROCK) {
                    locationInSphere.getBlock().setType(Material.AIR);
                }
            }
        }

        GuildUtils.spawnHeart(guild);
        player.sendMessage(messages.adminGuildRelocated.replace("{GUILD}", guild.getName()).replace("{REGION}", region.getName()));
    }

}
