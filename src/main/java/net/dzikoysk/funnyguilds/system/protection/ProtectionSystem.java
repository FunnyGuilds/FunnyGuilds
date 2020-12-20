package net.dzikoysk.funnyguilds.system.protection;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public final class ProtectionSystem {

    public static boolean isProtected(Player player, Location location, boolean includeBuildLock) {
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
            if (includeBuildLock && !guild.canBuild()) {
                player.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().regionExplodeInteract.replace("{TIME}",
                        Long.toString(TimeUnit.MILLISECONDS.toSeconds(guild.getBuild() - System.currentTimeMillis()))));
                return true;
            }

            if (location.equals(region.getHeart())) {
                Pair<Material, Byte> heartMaterial = FunnyGuilds.getInstance().getPluginConfiguration().createMaterial;
                return heartMaterial != null && heartMaterial.getLeft() != Material.AIR;
            }
            
            return false;
        }

        player.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().regionOther);
        return true;
    }

    public static boolean isProtected(Player player, Location loc) {
        return isProtected(player, loc, false);
    }

}
