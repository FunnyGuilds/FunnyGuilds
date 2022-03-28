package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

/**
 * The migration fixes the inconsistency of the {@code freeCam-compensation}
 * key by renaming it to {@code freecam-compensation}.
 */
public class P0001_Fix_freecam_compensation_key_case extends NamedMigration {

    public P0001_Fix_freecam_compensation_key_case() {
        super(
                "Rename freeCam-compensation to freecam-compensation",
                move("freeCam-compensation", "freecam-compensation")
        );
    }
}
