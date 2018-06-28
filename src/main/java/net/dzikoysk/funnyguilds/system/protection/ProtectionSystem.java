package net.dzikoysk.funnyguilds.system.protection;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.concurrent.TimeUnit;

public final class ProtectionSystem {

    /*public static boolean center(Location loc) {
        Region region = RegionUtils.getAt(loc);
        if (region == null) {
            return false;
        }
        
        return region.getCenter().getBlock().getRelative(BlockFace.UP).getLocation().toVector().equals(loc.getBlock().getLocation().toVector());
    }*/

    public static boolean build(Player player, Location location, boolean build) {
        if (player == null || location == null) {
            return false;
        }
        
        if (player.hasPermission("funnyguilds.admin.build")) {
            return false;
        }
        
        Region region = RegionUtils.getAt(location);
        if (region == null) {
            return false;
        }
        
        Guild guild = region.getGuild();
        if (guild == null || guild.getName() == null) {
            return false;
        }
        
        User user = User.get(player);
        if (guild.getMembers().contains(user)) {
            if (build && !guild.canBuild()) {
                player.sendMessage(Messages.getInstance().regionExplodeInteract.replace("{TIME}",
                        Long.toString(TimeUnit.MILLISECONDS.toSeconds(guild.getBuild() - System.currentTimeMillis()))));
                return true;
            } else if (location.equals(region.getCenter().getBlock().getRelative(BlockFace.DOWN).getLocation())) {
                MaterialData md = Settings.getConfig().createMaterialData;
                if (md != null && md.getItemType() != Material.AIR) {
                    return true;
                }
            }
            
            return false;
        }
        
        player.sendMessage(Messages.getInstance().regionOther);
        return true;
    }

    public static boolean build(Player player, Location loc) {
        return build(player, loc, false);
    }
}
