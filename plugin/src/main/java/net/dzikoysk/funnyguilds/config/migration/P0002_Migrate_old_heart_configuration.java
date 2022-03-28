package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

public class P0002_Migrate_old_heart_configuration extends NamedMigration {

    public P0002_Migrate_old_heart_configuration() {
        super(
                "Migrate old top-level keys to heart-configuration subconfig",
                move("create-type", "heart-configuration.create-type"),
                move("create-center-y", "heart-configuration.create-center-y"),
                move("create-center-sphere", "heart-configuration.create-center-sphere"),
                move("paste-schematic-on-creation", "heart-configuration.paste-schematic-on-creation"),
                move("guild-schematic-file-name", "heart-configuration.guild-schematic-file-name"),
                move("paste-schematic-with-air", "heart-configuration.paste-schematic-with-air")
        );
    }
}
