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
import net.dzikoysk.funnyguilds.util.StringUtils;
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
        MessagesConfig m = Messages.getInstance();
        Player player = (Player) sender;

        if (!player.hasPermission("funnyguilds.admin")) {
            player.sendMessage(m.permission);
            return;
        }

        if (args.length < 1) {
            player.sendMessage(StringUtils.colored("&cPodaj tag gildii!"));
            return;
        }

        String tag = args[0];
        if (!GuildUtils.tagExists(tag)) {
            player.sendMessage(StringUtils.colored("&cGildia o takim tagu nie istnieje!"));
            return;
        }

        PluginConfig s = Settings.getConfig();
        Location loc = player.getLocation();
        if (s.createCenterY != 0) {
            loc.setY(s.createCenterY);
        }

        int d = s.regionSize + s.createDistance;
        if (s.enlargeItems != null) {
            d = s.enlargeItems.size() * s.enlargeSize + d;
        }

        if (d > player.getWorld().getSpawnLocation().distance(loc)) {
            player.sendMessage(m.createSpawn
                                       .replace("{DISTANCE}", Integer.toString(d))
            );
            return;
        }
        if (RegionUtils.isNear(loc)) {
            player.sendMessage(StringUtils.colored("&cW poblizu znajduje sie jakas gildia, musisz poszukac innego miejsca!"));
            return;
        }

        Guild guild = GuildUtils.byTag(tag);
        Region region = RegionUtils.get(guild.getRegion());
        if (region == null) {
            region = new Region(guild, loc, s.regionSize);
        } else {
            if (s.createStringMaterial.equalsIgnoreCase("ender crystal")) {
                EntityUtil.despawn(guild);
            } else {
                Block block = region.getCenter().getBlock().getRelative(BlockFace.DOWN);
                if (block.getLocation().getBlockY() > 1) {
                    block.setType(Material.AIR);
                }
            }
            region.setCenter(loc);
        }
        if (s.createCenterSphere) {
            List<Location> sphere = SpaceUtils.sphere(loc, 3, 3, false, true, 0);
            for (Location l : sphere) {
                if (l.getBlock().getType() != Material.BEDROCK) {
                    l.getBlock().setType(Material.AIR);
                }
            }
        }
        if (s.createMaterial != null && s.createMaterial != Material.AIR) {
            loc.getBlock().getRelative(BlockFace.DOWN).setType(s.createMaterial);
        } else if (s.createStringMaterial.equalsIgnoreCase("ender crystal")) {
            EntityUtil.spawn(guild);
        }

        player.sendMessage(StringUtils.colored("&7Przeniesiono teren gildii &a" + guild.getName() + "&7!"));
        return;
    }

}
