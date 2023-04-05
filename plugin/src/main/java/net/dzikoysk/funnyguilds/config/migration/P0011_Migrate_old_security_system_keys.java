package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

public class P0011_Migrate_old_security_system_keys extends NamedMigration {

    public P0011_Migrate_old_security_system_keys() {
        super(
                "Migrate old top-level keys to security system subconfig",
                move("system-security-enable", "security-system.enabled"),
                move("reach-compensation", "security-system.reach.compensation"),
                move("freecam-compensation", "security-system.freecam.compensation")
        );
    }

}
