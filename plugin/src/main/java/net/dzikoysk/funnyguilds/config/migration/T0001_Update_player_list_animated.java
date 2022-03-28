package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;
import java.util.Collection;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.match;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.update;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.when;

/**
 * The migration makes sure when the tablist.yml value of {@code pages}
 * is {@code []} to set the {@code player-list-animated} to {@code false}.
 */
public class T0001_Update_player_list_animated extends NamedMigration {

    public T0001_Update_player_list_animated() {
        super(
                "Preserve behavior of empty tablist pages by updating player-list-animated",
                when(
                        match("pages", (Collection<?> value) -> value.isEmpty()),
                        update("player-list-animated", (old) -> false)
                )
        );
    }
}
