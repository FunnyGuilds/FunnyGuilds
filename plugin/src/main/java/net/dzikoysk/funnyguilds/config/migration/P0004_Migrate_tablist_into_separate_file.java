package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.builtin.NamedMigration;
import eu.okaeri.configs.migrate.view.RawConfigView;
import java.io.File;
import java.util.Objects;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.ConfigurationFactory;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;

public class P0004_Migrate_tablist_into_separate_file extends NamedMigration {

    private static final ConfigurationFactory CONFIGURATION_FACTORY = new ConfigurationFactory();

    public P0004_Migrate_tablist_into_separate_file() {
        super(
                "Migrate old config.yml tablist into tablist.yml",
                moveToTablistConfig("player-list"),
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

            File tablistConfigurationFile = FunnyGuilds.getInstance().getTablistConfigurationFile();
            TablistConfiguration tablistConfig = CONFIGURATION_FACTORY.createTablistConfiguration(tablistConfigurationFile);
            RawConfigView tablistView = new RawConfigView(tablistConfig);

            Object targetValue = view.remove(localKey);
            Object oldValue = tablistView.set(tablistKey, targetValue);

            tablistConfig.save();

            return !Objects.equals(targetValue, oldValue);
        };
    }
}
