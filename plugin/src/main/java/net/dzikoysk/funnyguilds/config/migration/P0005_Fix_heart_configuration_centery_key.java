package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

public class P0005_Fix_heart_configuration_centery_key extends NamedMigration {

    public P0005_Fix_heart_configuration_centery_key() {
        super(
                "Rename heart-configuration's use-player-position-for-centery to use-player-position-for-center-y",
                move("heart-configuration.use-player-position-for-centery", "heart-configuration.use-player-position-for-center-y")
        );
    }
}
