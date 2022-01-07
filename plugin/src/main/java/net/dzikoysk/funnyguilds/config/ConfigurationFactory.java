package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.validator.okaeri.OkaeriValidator;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.io.File;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistPageSerializer;
import net.dzikoysk.funnyguilds.config.transformer.DecolorTransformer;
import net.dzikoysk.funnyguilds.config.transformer.FunnyTimeTransformer;
import net.dzikoysk.funnyguilds.config.transformer.MaterialTransformer;
import net.dzikoysk.funnyguilds.config.transformer.SimpleDateFormatTransformer;

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
                registry.register(new DecolorTransformer());
                registry.register(new MaterialTransformer());
                registry.register(new FunnyTimeTransformer());
            });
            it.withBindFile(pluginConfigurationFile);
            it.saveDefaults();
            it.load(true);
        });
    }

    public TablistConfiguration createTablistConfiguration(File tablistConfigurationFile) {
        return ConfigManager.create(TablistConfiguration.class, (it) -> {
            it.withConfigurer(new OkaeriValidator(new YamlBukkitConfigurer(), true), new SerdesCommons());
            it.withSerdesPack(registry -> registry.register(new TablistPageSerializer()));
            it.withBindFile(tablistConfigurationFile);
            it.saveDefaults();
            it.load(true);
        });
    }

}
