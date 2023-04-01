package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;
import java.util.Collections;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.match;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.update;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.when;

public class P0010_Migrate_old_enlarge_enable_option extends NamedMigration {

    public P0010_Migrate_old_enlarge_enable_option() {
        super(
                "Clear enlarge-items when enlarge-enable is false",
                when(
                        match("enlarge-enable", v -> v instanceof Boolean && !(Boolean) v),
                        update("enlarge-items", (old) -> Collections.emptyList())
                )
        );
    }

}
