package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.builtin.NamedMigration;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration.Center.Height;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

public class P0008_Migrate_old_heart_center_configuration extends NamedMigration {

    public P0008_Migrate_old_heart_center_configuration() {
        super(
                "Migrate old heart center configuration to center subconfig",
                moveAndTransform("heart-configuration.use-player-position-for-center-y", "heart-configuration.center.height", oldValue -> {
                    if (oldValue instanceof Boolean && (Boolean) oldValue) {
                        return Height.PLAYER;
                    }
                    return Height.FIXED;
                }),
                move("heart-configuration.create-center-y", "heart-configuration.center.fixed-height")
        );
    }

    private static ConfigMigration moveAndTransform(String oldKey, String newKey, Function<Object, Object> transformer) {
        return (config, view) -> {
            if (!view.exists(oldKey)) {
                return false;
            }

            Object oldValue = view.remove(oldKey);
            Object targetValue = transformer.apply(oldValue);
            view.set(newKey, targetValue);
            return true;
        };
    }

}
