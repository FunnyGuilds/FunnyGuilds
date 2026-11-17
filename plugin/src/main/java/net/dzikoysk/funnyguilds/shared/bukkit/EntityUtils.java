package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.Optionals;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;

public final class EntityUtils {

    private EntityUtils() {
    }

    public static Optional<Player> getDamageAttacker(EntityDamageByEntityEvent event) {
        DamageSource source = event.getDamageSource();
        if (source.getDirectEntity() instanceof EnderCrystal) {
            return getCrystalAttacker(source);
        }

        return getAttacker(event.getDamager());
    }

    /** Resolves the detonator from the server's source, never from the crystal's placer. */
    public static Optional<Player> getCrystalAttacker(DamageSource source) {
        if (!(source.getDirectEntity() instanceof EnderCrystal)) {
            return Optional.empty();
        }

        return Optional.ofNullable(source.getCausingEntity())
                .filter(Player.class::isInstance)
                .map(Player.class::cast);
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
