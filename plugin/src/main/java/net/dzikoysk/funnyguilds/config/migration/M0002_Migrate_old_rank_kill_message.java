package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.builtin.NamedMigration;
import eu.okaeri.configs.migrate.view.RawConfigView;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.SerdesContext;
import java.io.File;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.ConfigurationFactory;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import org.jetbrains.annotations.Nullable;
import dev.peri.yetanothermessageslibrary.adventure.MiniComponent;
import dev.peri.yetanothermessageslibrary.adventure.RawComponent;
import dev.peri.yetanothermessageslibrary.message.SendableMessage;
import dev.peri.yetanothermessageslibrary.message.holder.impl.TitleHolder;

public class M0002_Migrate_old_rank_kill_message extends NamedMigration {

    public M0002_Migrate_old_rank_kill_message() {
        super(
                "Migrate old rank kill message into new format (YetAnotherMessagesLibrary)",
                moveMessage("rankKillTitle", "rankKillMessage")
        );
    }

    private static ConfigMigration moveMessage(String oldKey, String newKey) {
        return ((config, view) -> {
            if (!view.exists(oldKey)) {
                return false;
            }

            TitleHolder.Builder builder = TitleHolder.builder();
            builder.times(
                    (Integer) getConfigValueOrDefault("notification-title-fade-in", 10),
                    (Integer) getConfigValueOrDefault("notification-title-stay", 10),
                    (Integer) getConfigValueOrDefault("notification-title-fade-out", 10)
            );

            RawComponent title = getRawComponent(view, oldKey);
            if (title != null) {
                builder.title(title);
            }

            RawComponent subTitle = getRawComponent(view, FunnyFormatter.format(oldKey, "Title", "Subtitle"));
            if (subTitle != null) {
                builder.subTitle(subTitle);
            }

            Configurer configurer = config.getConfigurer();
            Object value = configurer.simplify(
                    SendableMessage.of(builder.build()),
                    GenericsDeclaration.of(SendableMessage.class),
                    SerdesContext.of(configurer),
                    true
            );
            view.set(newKey, value);
            return true;
        });
    }

    @Nullable
    private static RawComponent getRawComponent(RawConfigView view, String key) {
        if (!view.exists(key)) {
            return null;
        }
        String message = (String) view.remove(key);
        return MiniComponent.of(message);
    }

    private static Object getConfigValueOrDefault(String key, Object defaultValue) {
        File configurationFile = FunnyGuilds.getInstance().getPluginConfigurationFile();
        PluginConfiguration pluginConfig = ConfigurationFactory.createPluginConfiguration(configurationFile);
        RawConfigView configView = new RawConfigView(pluginConfig);

        Object value = configView.get(key);
        if (value == null) {
            return defaultValue;
        }
        pluginConfig.save();
        return value;
    }

}
