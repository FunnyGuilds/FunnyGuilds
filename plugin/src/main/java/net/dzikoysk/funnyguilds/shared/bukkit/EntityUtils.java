package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.Optionals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;

public final class EntityUtils {

    private EntityUtils() {
    }

    public static Optional<Player> getAttacker(Entity damager) {
        if (damager instanceof Player) {
            return Optional.of((Player) damager);
        }

        if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();

            if (shooter instanceof Player) {
                return Optional.of((Player) shooter);
            }
        }

        return Optional.empty();
    }

    @Nullable
    public static EntityType parseEntityType(String stringEntity, boolean allowNullReturn, boolean logUnknown) {
        if (stringEntity == null) {
            if (logUnknown) {
                FunnyGuilds.getPluginLogger().parser("Unknown entity: null");
            }
            return allowNullReturn ? null : EntityType.UNKNOWN;
        }

        EntityType entityType = Optionals.attempt(IllegalArgumentException.class, () -> {
            return EntityType.valueOf(stringEntity.toUpperCase(Locale.ROOT).replace(" ", "_"));
        }).orElse(null);
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
        return Arrays.stream(stringEntities)
                .map(stringEntity -> parseEntityType(stringEntity, allowNullReturn))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

}
