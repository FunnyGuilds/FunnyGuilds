package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

public class P0006_Migrate_old_scoreboard_configuration extends NamedMigration {
    public P0006_Migrate_old_scoreboard_configuration() {
        super(
                "Migrate old top-level keys to scoreboard subconfig",
                move("use-shared-scoreboard", "scoreboard.use-shared-scoreboard"),
                move("guild-tag-enabled", "scoreboard.nametag.enabled"),
                move("dummy-enable", "scoreboard.dummy.enabled"),
                move("dummy-suffix", "scoreboard.dummy.suffix")
        );
    }
}
