package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.builtin.NamedMigration;
import eu.okaeri.configs.migrate.view.RawConfigView;
import java.io.File;
import java.util.Objects;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.ConfigurationFactory;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.when;

/**
 * The migration moves old config.yml tablist config entries into
 * the new separate tablist.yml file whilst preserving the behavior
 * of static (non-animated) tablist.
 *
 * New users are not affected by this migration and will have
 * the tablist animations enabled by default.
 */
public class P0004_Migrate_tablist_into_separate_file extends NamedMigration {

    public P0004_Migrate_tablist_into_separate_file() {
        super(
                "Migrate old config.yml tablist into tablist.yml",
                when(
                        moveToTablistConfig("player-list"),
                        (config, view) -> {
                            updateTablistConfig("player-list-animated", false);
                            return true;
                        }
                ),
                moveToTablistConfig("player-list-header"),
                moveToTablistConfig("player-list-footer"),
                moveToTablistConfig("player-list-ping"),
                moveToTablistConfig("player-list-fill-cells"),
                moveToTablistConfig("player-list-enable"),
                moveToTablistConfig("player-list-update-interval"),
                moveToTablistConfig("player-list-use-relationship-colors")
        );
    }

    private static ConfigMigration moveToTablistConfig(String key) {
        return moveToTablistConfig(key, key);
    }

    private static ConfigMigration moveToTablistConfig(String localKey, String tablistKey) {
        return (config, view) -> {
            if (!view.exists(localKey)) {
                return false;
            }

            Object targetValue = view.remove(localKey);
            Object oldValue = updateTablistConfig(tablistKey, targetValue);
            return !Objects.equals(targetValue, oldValue);
        };
    }

    private static Object updateTablistConfig(String key, Object value) {
        File tablistConfigurationFile = FunnyGuilds.getInstance().getTablistConfigurationFile();
        TablistConfiguration tablistConfig = ConfigurationFactory.createTablistConfiguration(tablistConfigurationFile);
        RawConfigView tablistView = new RawConfigView(tablistConfig);

        Object oldValue = tablistView.set(key, value);
        tablistConfig.save();

        return oldValue;
    }

}
