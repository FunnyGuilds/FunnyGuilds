package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.validator.okaeri.OkaeriValidator;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.io.File;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.migration.P0001_Fix_freecam_compensation_key_case;
import net.dzikoysk.funnyguilds.config.migration.P0002_Migrate_old_heart_configuration;
import net.dzikoysk.funnyguilds.config.migration.P0003_Migrate_old_tnt_protection_configuration;
import net.dzikoysk.funnyguilds.config.migration.P0004_Migrate_tablist_into_separate_file;
import net.dzikoysk.funnyguilds.config.migration.P0005_Fix_heart_configuration_centery_key;
import net.dzikoysk.funnyguilds.config.migration.T0001_Update_player_list_animated;
import net.dzikoysk.funnyguilds.config.serdes.DecolorTransformer;
import net.dzikoysk.funnyguilds.config.serdes.FunnyItemStackTransformer;
import net.dzikoysk.funnyguilds.config.serdes.FunnyTimeTransformer;
import net.dzikoysk.funnyguilds.config.serdes.MaterialTransformer;
import net.dzikoysk.funnyguilds.config.serdes.NumberRangeTransformer;
import net.dzikoysk.funnyguilds.config.serdes.RangeFormattingTransformer;
import net.dzikoysk.funnyguilds.config.serdes.RawStringTransformer;
import net.dzikoysk.funnyguilds.config.serdes.SimpleDateFormatTransformer;
import net.dzikoysk.funnyguilds.config.serdes.SkinTextureSerializer;
import net.dzikoysk.funnyguilds.config.serdes.VectorSerializer;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistPageSerializer;

public final class ConfigurationFactory {

    public MessageConfiguration createMessageConfiguration(File messageConfigurationFile) {
        return ConfigManager.create(MessageConfiguration.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withSerdesPack(registry -> {
                registry.register(new DecolorTransformer());
                registry.register(new SimpleDateFormatTransformer());
            });
            it.withBindFile(messageConfigurationFile);
            it.saveDefaults();
            it.load(true);
        });
    }

    public PluginConfiguration createPluginConfiguration(File pluginConfigurationFile) {
        return ConfigManager.create(PluginConfiguration.class, (it) -> {
            it.withConfigurer(new OkaeriValidator(new YamlBukkitConfigurer(), true), new SerdesCommons());
            it.withSerdesPack(registry -> {
                registry.register(new RawStringTransformer());
                registry.register(new MaterialTransformer());
                registry.register(new FunnyItemStackTransformer());
                registry.register(new FunnyTimeTransformer());
                registry.register(new NumberRangeTransformer());
                registry.register(new RangeFormattingTransformer());
                registry.register(new VectorSerializer());
            });
            it.withBindFile(pluginConfigurationFile);
            it.withLogger(FunnyGuilds.getInstance().getLogger());
            it.saveDefaults();
            it.load(true);
            it.migrate(
                    new P0001_Fix_freecam_compensation_key_case(),
                    new P0002_Migrate_old_heart_configuration(),
                    new P0003_Migrate_old_tnt_protection_configuration(),
                    new P0004_Migrate_tablist_into_separate_file(),
                    new P0005_Fix_heart_configuration_centery_key()
            );
        });
    }

    public TablistConfiguration createTablistConfiguration(File tablistConfigurationFile) {
        return ConfigManager.create(TablistConfiguration.class, (it) -> {
            it.withConfigurer(new OkaeriValidator(new YamlBukkitConfigurer(), true), new SerdesCommons());
            it.withSerdesPack(registry -> {
                registry.register(new NumberRangeTransformer());
                registry.register(new TablistPageSerializer());
                registry.register(new SkinTextureSerializer());
            });
            it.withBindFile(tablistConfigurationFile);
            it.saveDefaults();
            it.load(true);
            it.migrate(
                    new T0001_Update_player_list_animated()
            );
        });
    }

}
