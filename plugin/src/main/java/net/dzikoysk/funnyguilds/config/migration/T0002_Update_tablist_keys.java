package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

public class T0002_Update_tablist_keys extends NamedMigration {

    public T0002_Update_tablist_keys() {
        super(
                "Migrate old tablist keys to new ones",
                move("player-list-enable", "enabled"),
                move("player-list", "cells"),
                move("player-list-header", "header"),
                move("player-list-footer", "footer"),
                move("player-list-animated", "animated"),
                move("player-list-ping", "cells-ping"),
                move("player-list-fill-cells", "fill-cells"),
                move("player-list-update-interval", "update-interval"),
                move("player-list-use-relationship-colors", "use-relationship-colors")
        );
    }

}
