package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

public class P0003_Migrate_old_tnt_protection_configuration extends NamedMigration {

    public P0003_Migrate_old_tnt_protection_configuration() {
        super(
                "Migrate old top-level keys to tnt-protection subconfig",
                move("guild-tnt-protection-enabled", "tnt-protection.time.enabled"),
                move("guild-tnt-protection-global", "tnt-protection.time.enabled-global"),
                move("guild-tnt-protection-start-time", "tnt-protection.time.start-time"),
                move("guild-tnt-protection-end-time", "tnt-protection.time.end-time")
        );
    }
}
