package net.dzikoysk.funnyguilds.util.commons.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import panda.std.Option;

public final class EntityUtils {

    private EntityUtils() {}

    public static Option<Player> getAttacker(Entity damager) {
        if (damager instanceof Player) {
            return Option.of((Player) damager);
        }

        if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();

            if (shooter instanceof Player) {
                return Option.of((Player) shooter);
            }
        }

        return Option.none();
    }

}
