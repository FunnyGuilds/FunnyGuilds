package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;
import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.view.RawConfigView;
import java.io.File;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.ConfigurationFactory;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;

/**
 * Base class for FunnyGuilds migrations that need to read from other configs.
 * Provides convenient accessors for cross-config migrations.
 */
public abstract class FunnyMigration extends NamedMigration {

    protected FunnyMigration(String name, ConfigMigration... migrations) {
        super(name, migrations);
    }

    /**
     * Gets a view of the plugin configuration for reading values during migration.
     */
    protected static RawConfigView plugin() {
        File configFile = FunnyGuilds.getInstance().getPluginConfigurationFile();
        PluginConfiguration config = ConfigurationFactory.createPluginConfiguration(configFile);
        return new RawConfigView(config);
    }

    /**
     * Gets a view of the tablist configuration for reading values during migration.
     */
    protected static RawConfigView tablist() {
        File configFile = FunnyGuilds.getInstance().getTablistConfigurationFile();
        TablistConfiguration config = ConfigurationFactory.createTablistConfiguration(configFile);
        return new RawConfigView(config);
    }

}
