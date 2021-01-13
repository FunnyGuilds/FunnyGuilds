package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildMoveEvent;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.util.nms.GuildEntityHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class MoveCommand {

    @FunnyCommand(
        name = "${admin.move.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        Player player = (Player) sender;

        if (!config.regionsEnabled) {
            player.sendMessage(messages.regionsDisabled);
            return;
        }
        
        if (args.length < 1) {
            player.sendMessage(messages.generalNoTagGiven);
            return;
        }

        Guild guild = GuildUtils.getByTag(args[0]);

        if (guild == null) {
            player.sendMessage(messages.generalNoGuildFound);
            return;
        }

        Location location = player.getLocation();

        if (config.createCenterY != 0) {
            location.setY(config.createCenterY);
        }

        int distance = config.regionSize + config.createDistance;

        if (config.enlargeItems != null) {
            distance = config.enlargeItems.size() * config.enlargeSize + distance;
        }

        if (distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), location)) {
            player.sendMessage(messages.createSpawn.replace("{DISTANCE}", Integer.toString(distance)));
            return;
        }

        if (RegionUtils.isNear(location)) {
            player.sendMessage(messages.createIsNear);
            return;
        }

        User admin = User.get(player);
        if (!SimpleEventHandler.handle(new GuildMoveEvent(EventCause.ADMIN, admin, guild, location))) {
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
