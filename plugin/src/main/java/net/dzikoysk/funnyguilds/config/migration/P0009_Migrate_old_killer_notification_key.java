package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

public class P0009_Migrate_old_killer_notification_key extends NamedMigration {

    public P0009_Migrate_old_killer_notification_key() {
        super(
                "Migrate old key for killer notification",
                move("display-title-notification-for-killer", "display-notification-for-killer")
        );
    }

}
