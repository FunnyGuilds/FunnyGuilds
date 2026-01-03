package net.dzikoysk.funnyguilds.config;

import dev.peri.yetanothermessageslibrary.config.serdes.YAMLSerdes;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.validator.okaeri.OkaeriValidator;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.io.File;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.serdes.ColorSerializer;
import net.dzikoysk.funnyguilds.config.serdes.DecolorTransformer;
import net.dzikoysk.funnyguilds.config.serdes.EntityTypeTransformer;
import net.dzikoysk.funnyguilds.config.serdes.FunnyPatternTransformer;
import net.dzikoysk.funnyguilds.config.serdes.FunnyTimeFormatterTransformer;
import net.dzikoysk.funnyguilds.config.serdes.FunnyTimeTransformer;
import net.dzikoysk.funnyguilds.config.serdes.ItemStackTransformer;
import net.dzikoysk.funnyguilds.config.serdes.MaterialTransformer;
import net.dzikoysk.funnyguilds.config.serdes.NumberRangeTransformer;
import net.dzikoysk.funnyguilds.config.serdes.RangeFormattingTransformer;
import net.dzikoysk.funnyguilds.config.serdes.SkinTextureSerializer;
import net.dzikoysk.funnyguilds.config.serdes.TextColorSerializer;
import net.dzikoysk.funnyguilds.config.serdes.VectorSerializer;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistPageSerializer;

public final class ConfigurationFactory {

    private ConfigurationFactory() {
    }

    public static MessageConfiguration createMessageConfiguration(File messageConfigurationFile) {
        return ConfigManager.create(MessageConfiguration.class, (it) -> {
            it.configure(opt -> {
                opt.configurer(new YamlBukkitConfigurer());
                opt.serdes(
                    new DecolorTransformer(),
                    new FunnyTimeFormatterTransformer(),
                    new TextColorSerializer(),
                    new YAMLSerdes()
                );
                opt.logger(FunnyGuilds.getInstance().getLogger());
                opt.bindFile(messageConfigurationFile);
            });
            it.saveDefaults();
            it.load(true);
        });
    }

    public static PluginConfiguration createPluginConfiguration(File pluginConfigurationFile) {
        return ConfigManager.create(PluginConfiguration.class, (it) -> {
            it.configure(opt -> {
                opt.configurer(new YamlBukkitConfigurer(), new SerdesCommons());
                opt.validator(new OkaeriValidator(true));
                opt.serdes(
                    new YAMLSerdes(),
                    new TextColorSerializer(),
                    new ColorSerializer(),
                    new MaterialTransformer(),
                    new ItemStackTransformer(),
                    new EntityTypeTransformer(),
                    new VectorSerializer(),
                    new FunnyTimeTransformer(),
                    new FunnyPatternTransformer(),
                    new RangeFormattingTransformer()
                );
                opt.bindFile(pluginConfigurationFile);
                opt.logger(FunnyGuilds.getInstance().getLogger());
                opt.errorComments(true);
            });
            it.saveDefaults();
            it.load(true);
        });
    }

    public static TablistConfiguration createTablistConfiguration(File tablistConfigurationFile) {
        return ConfigManager.create(TablistConfiguration.class, (it) -> {
            it.configure(opt -> {
                opt.configurer(new YamlBukkitConfigurer(), new SerdesCommons());
                opt.validator(new OkaeriValidator(true));
                opt.serdes(
                    new NumberRangeTransformer(),
                    new TablistPageSerializer(),
                    new SkinTextureSerializer(),
                    new YAMLSerdes()
                );
                opt.bindFile(tablistConfigurationFile);
                opt.logger(FunnyGuilds.getInstance().getLogger());
                opt.errorComments(true);
            });
            it.saveDefaults();
            it.load(true);
        });
    }

}
