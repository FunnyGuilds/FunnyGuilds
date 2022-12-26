package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

public class P0007_Migrate_old_relational_tag_configuration extends NamedMigration {

    public P0007_Migrate_old_relational_tag_configuration() {
        super(
                "Migrate old top-level keys to new relational tag configuration",
                move("prefix-our", "relational-tag.our"),
                move("prefix-allies", "relational-tag.allies"),
                move("prefix-enemies", "relational-tag.enemies"),
                move("prefix-other", "relational-tag.other")
        );
    }

}
