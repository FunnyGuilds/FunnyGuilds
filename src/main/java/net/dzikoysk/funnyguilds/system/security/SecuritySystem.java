package net.dzikoysk.funnyguilds.system.security;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.system.security.cheat.SecurityFreeCam;
import net.dzikoysk.funnyguilds.system.security.cheat.SecurityReach;
import net.dzikoysk.funnyguilds.util.FunnyBox;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public final class SecuritySystem {

    private static final double ADDITIONAL_SNEAKING_HEIGHT_CURSOR = 0.35;

    private final Map<User, Integer> playersViolationLevel = new HashMap<>();
    private final FunnyGuilds plugin;

    public SecuritySystem(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public boolean onHitCrystal(Player player, Guild guild) {
        scan(player, SecurityType.GUILD, guild);
        return isBlocked(plugin.getUserManager().getUser(player));
    }

    private void scan(Player player, SecurityType securityType, Object... values) {
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

        if (securityType == SecurityType.GUILD) {
            if (guild == null) {
                return;
            }

            Location center = guild.getRegion().getCenter();

            double x = center.getX() + 0.5;
            double y = center.getY();
            double z = center.getZ() + 0.5;

            Location eye = player.getEyeLocation();
            Vector direction = eye.getDirection();
            Vector origin = (player.isSneaking() && !Reflections.USE_PRE_9_METHODS)
                    ? eye.add(0.0, ADDITIONAL_SNEAKING_HEIGHT_CURSOR, 0.0).toVector()
                    : eye.toVector();
            FunnyBox funnyBox = "ender_crystal".equalsIgnoreCase(config.createType)
                    ? new FunnyBox(x - 1.0, y - 1.0 ,z - 1.0, x + 1.0, y + 1.0 ,z + 1.0)
                    : FunnyBox.of(player.getWorld().getBlockAt(center));

            FunnyBox.RayTraceResult rayTraceResult = funnyBox.rayTrace(origin, direction, 6);
            if (rayTraceResult == null) {
                return;
            }

            Vector hitPoint = rayTraceResult.getHitPosition();
            double distance = hitPoint.distance(origin);

            SecurityFreeCam.on(player, origin, hitPoint, distance, this);
            SecurityReach.on(player, distance, this);
            return;
        }

        throw new IllegalArgumentException("unknown securityType: " + securityType);
    }

    public void addViolationLevel(User user) {
        playersViolationLevel.put(user, playersViolationLevel.getOrDefault(user, 0) + 1);

        Bukkit.getScheduler().runTaskLater(FunnyGuilds.getInstance(), () -> playersViolationLevel.remove(user), 18000);
    }

    public boolean isBlocked(User user) {
        return playersViolationLevel.getOrDefault(user, 0) > 1;
    }


}
