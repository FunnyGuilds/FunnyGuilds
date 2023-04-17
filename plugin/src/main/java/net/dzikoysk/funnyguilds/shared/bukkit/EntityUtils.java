package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import panda.std.stream.PandaStream;

public final class EntityUtils {

    private EntityUtils() {
    }

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

    @Nullable
    public static EntityType parseEntityType(String stringEntity, boolean allowNullReturn, boolean logUnknown) {
        if (stringEntity == null) {
            if (logUnknown) {
                FunnyGuilds.getPluginLogger().parser("Unknown entity: null");
            }
            return allowNullReturn ? null : EntityType.UNKNOWN;
        }

        EntityType entityType = Option.attempt(IllegalArgumentException.class, () -> {
            return EntityType.valueOf(FunnyFormatter.format(stringEntity.toUpperCase(Locale.ROOT), " ", "_"));
        }).orNull();

        if (entityType != null) {
            return entityType;
        }

        entityType = EntityType.fromName(stringEntity);
        if (entityType != null) {
            return entityType;
        }

        if (logUnknown) {
            FunnyGuilds.getPluginLogger().parser("Unknown entity: " + stringEntity);
        }
        return allowNullReturn ? null : EntityType.UNKNOWN;
    }

    @Nullable
    public static EntityType parseEntityType(String stringEntity, boolean allowNullReturn) {
        return parseEntityType(stringEntity, allowNullReturn, true);
    }

    public static Set<EntityType> parseEntityTypes(boolean allowNullReturn, String... stringEntities) {
        return PandaStream.of(stringEntities)
                .map(stringEntity -> parseEntityType(stringEntity, allowNullReturn))
                .filter(Objects::nonNull)
                .toSet();
    }

}
