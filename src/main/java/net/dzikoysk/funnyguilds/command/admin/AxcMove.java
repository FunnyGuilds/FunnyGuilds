package net.dzikoysk.funnyguilds.command.admin;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

public class AxcMove implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) s;

        if (args.length < 1) {
            p.sendMessage(m.adminNoTagGiven);
            return;
        }

        if (!GuildUtils.tagExists(args[0])) {
            p.sendMessage(m.adminNoGuildFound);
            return;
        }

        PluginConfig pc = Settings.getConfig();
        Location loc = p.getLocation();
        if (pc.createCenterY != 0) {
            loc.setY(pc.createCenterY);
        }

        int d = pc.regionSize + pc.createDistance;
        if (pc.enlargeItems != null) {
            d = pc.enlargeItems.size() * pc.enlargeSize + d;
        }

        if (d > p.getWorld().getSpawnLocation().distance(loc)) {
            p.sendMessage(m.createSpawn.replace("{DISTANCE}", Integer.toString(d)));
            return;
        }

        if (RegionUtils.isNear(loc)) {
            p.sendMessage(m.createIsNear);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);
        Region region = RegionUtils.get(guild.getRegion());
        
        if (region == null) {
            region = new Region(guild, loc, pc.regionSize);
        } else {
            if (pc.createStringMaterial.equalsIgnoreCase("ender crystal")) {
                EntityUtil.despawn(guild);
            } else {
                Block block = region.getCenter().getBlock().getRelative(BlockFace.DOWN);
                if (block.getLocation().getBlockY() > 1) {
                    block.setType(Material.AIR);
                }
            }
            
            region.setCenter(loc);
        }
        
        if (pc.createCenterSphere) {
            List<Location> sphere = SpaceUtils.sphere(loc, 3, 3, false, true, 0);
            for (Location l : sphere) {
                if (l.getBlock().getType() != Material.BEDROCK) {
                    l.getBlock().setType(Material.AIR);
                }
            }
        }
        
        if (pc.createMaterial != null && pc.createMaterial != Material.AIR) {
            loc.getBlock().getRelative(BlockFace.DOWN).setType(pc.createMaterial);
        } else if (pc.createStringMaterial.equalsIgnoreCase("ender crystal")) {
            EntityUtil.spawn(guild);
        }

        p.sendMessage(m.adminGuildRelocated.replace("{GUILD}", guild.getName()));
    }
}
