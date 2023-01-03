package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration.Center.Height;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.update;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.when;

public class P0008_Migrate_old_heart_center_configuration extends NamedMigration {

    public P0008_Migrate_old_heart_center_configuration() {
        super(
                "Migrate old heart center configuration to center subconfig",
                when(move("heart.use-player-position-for-center-y", "heart.center.height"), update("heart.center.height", oldValue -> {
                    if (oldValue instanceof Boolean) {
                        return (Boolean) oldValue ? Height.PLAYER : Height.FIXED;
                    }
                    return Height.FIXED;
                })),
                move("heart.create-center-y", "heart.center.fixed-height")
        );
    }

}
