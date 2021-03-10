package net.dzikoysk.funnyguilds.system.security;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.system.security.cheat.SecurityFreeCam;
import net.dzikoysk.funnyguilds.system.security.cheat.SecurityReach;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public final class SecuritySystem {

    private static final Map<User, Integer> playersVL = new HashMap<>();

    public static boolean onHitCrystal(Player player, Guild guild) {
        scan(player, SecurityType.GUILD, guild);
        return SecurityUtils.isBlocked(User.get(player));
    }

    private static void scan(Player player, SecurityType securityType, Object... values) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            return;
        }

        if (!config.systemSecurityEnable) {
            return;
        }

        Guild guild = null;

        for (Object value : values) {
            if (value instanceof Guild) {
                guild = (Guild) value;
            }
        }

        if (securityType.equals(SecurityType.GUILD)) {
            if (guild == null) {
                return;
            }

            Region region = guild.getRegion();

            if (region == null) {
                return;
            }

            double x = region.getCenter().getX() + 0.5;
            double y = region.getCenter().getY();
            double z = region.getCenter().getZ() + 0.5;

            Vector origin = player.getEyeLocation().toVector();
            Vector direction = player.getEyeLocation().getDirection();
            BoundingBox boundingBox = new BoundingBox(x - 1.0, y - 1.0 ,z - 1.0, x + 1.0, y + 1.0 ,z + 1.0);
            RayTraceResult rayTraceResult = boundingBox.rayTrace(origin, direction, 6);

            if (rayTraceResult == null) {
                return;
            }

            Vector hitPoint = rayTraceResult.getHitPosition();
            double distance = hitPoint.distance(player.getEyeLocation().toVector());

            SecurityFreeCam.on(player, origin, hitPoint, distance);
            SecurityReach.on(player, distance);
        }
    }

    protected static Map<User, Integer> getPlayersVL() {
        return playersVL;
    }

}
