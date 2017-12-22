package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.SpaceUtils;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AxcMove implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        PluginConfig config = Settings.getConfig();
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(messages.generalNoTagGiven);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);

        if (guild == null) {
            player.sendMessage(messages.generalNoGuildFound);
            return;
        }

        Location location = player.getLocation();

        if (config.createCenterY != 0) {
            location.setY(config.createCenterY);
        }

        int d = config.regionSize + config.createDistance;

        if (config.enlargeItems != null) {
            d = config.enlargeItems.size() * config.enlargeSize + d;
        }

        if (d > player.getWorld().getSpawnLocation().distance(location)) {
            player.sendMessage(messages.createSpawn.replace("{DISTANCE}", Integer.toString(d)));
            return;
        }

        if (RegionUtils.isNear(location)) {
            player.sendMessage(messages.createIsNear);
            return;
        }

        Region region = RegionUtils.get(guild.getRegion());
        
        if (region == null) {
            region = new Region(guild, location, config.regionSize);
        } else {
            if (config.createStringMaterial.equalsIgnoreCase("ender crystal")) {
                EntityUtil.despawn(guild);
            } else {
                Block block = region.getCenter().getBlock().getRelative(BlockFace.DOWN);
                if (block.getLocation().getBlockY() > 1) {
                    block.setType(Material.AIR);
                }
            }
            
            region.setCenter(location);
        }
        
        if (config.createCenterSphere) {
            List<Location> sphere = SpaceUtils.sphere(location, 3, 3, false, true, 0);
            for (Location l : sphere) {
                if (l.getBlock().getType() != Material.BEDROCK) {
                    l.getBlock().setType(Material.AIR);
                }
            }
        }
        
        if (config.createMaterial != null && config.createMaterial != Material.AIR) {
            location.getBlock().getRelative(BlockFace.DOWN).setType(config.createMaterial);
        } else if (config.createStringMaterial.equalsIgnoreCase("ender crystal")) {
            EntityUtil.spawn(guild);
        }

        player.sendMessage(messages.adminGuildRelocated.replace("{GUILD}", guild.getName()).replace("{REGION}", region.getName()));
    }

}
