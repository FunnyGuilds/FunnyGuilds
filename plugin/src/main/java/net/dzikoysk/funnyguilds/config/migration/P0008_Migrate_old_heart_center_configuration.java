package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration.Center.Height;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

public class P0008_Migrate_old_heart_center_configuration extends NamedMigration {

    public P0008_Migrate_old_heart_center_configuration() {
        super(
                "Migrate old heart center configuration to center subconfig",
                move("heart-configuration.use-player-position-for-center-y", "heart-configuration.center.height", oldValue -> {
                    if (oldValue instanceof Boolean && (Boolean) oldValue) {
                        return Height.PLAYER;
                    }
                    return Height.FIXED;
                }),
                move("heart-configuration.create-center-y", "heart-configuration.center.fixed-height")
        );
    }

}
