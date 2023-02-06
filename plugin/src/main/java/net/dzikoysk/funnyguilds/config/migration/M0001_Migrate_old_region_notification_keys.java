package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.builtin.NamedMigration;
import eu.okaeri.configs.migrate.view.RawConfigView;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.SerdesContext;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.ConfigurationFactory;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.Nullable;
import pl.peridot.yetanothermessageslibrary.adventure.MiniComponent;
import pl.peridot.yetanothermessageslibrary.adventure.RawComponent;
import pl.peridot.yetanothermessageslibrary.message.SendableMessage;
import pl.peridot.yetanothermessageslibrary.message.holder.SendableHolder;
import pl.peridot.yetanothermessageslibrary.message.holder.impl.ActionBarHolder;
import pl.peridot.yetanothermessageslibrary.message.holder.impl.BossBarHolder;
import pl.peridot.yetanothermessageslibrary.message.holder.impl.ChatHolder;
import pl.peridot.yetanothermessageslibrary.message.holder.impl.TitleHolder;

public class M0001_Migrate_old_region_notification_keys extends NamedMigration {

    public M0001_Migrate_old_region_notification_keys() {
        super(
                "Migrate old region move notifications into new format (YetAnotherMessagesLibrary)",
                moveNotification("notification{TYPE}IntruderEnterGuildRegion"),
                moveNotification("notification{TYPE}EnterGuildRegion"),
                moveNotification("notification{TYPE}LeaveGuildRegion")
        );
    }

    private static ConfigMigration moveNotification(String key) {
        return (config, view) -> {
            List<SendableHolder> holders = new ArrayList<>();

            holders.add(prepareChatNotification(view, FunnyFormatter.format(key, "{TYPE}", "Chat")));
            holders.add(prepareActionBarNotification(view, FunnyFormatter.format(key, "{TYPE}", "Actionbar")));
            holders.add(prepareTitleNotification(view, FunnyFormatter.format(key, "{TYPE}", "Title")));
            holders.add(prepareBossBarNotification(view, FunnyFormatter.format(key, "{TYPE}", "Bossbar")));

            holders.removeIf(Objects::isNull);

            if (holders.isEmpty()) {
                return false;
            }

            List<String> notificationStyles = (List<String>) getConfigValueOrDefault("region-move-notification-style", new ArrayList<>());
            holders.removeIf(holder -> holder instanceof ChatHolder && !notificationStyles.contains("CHAT"));
            holders.removeIf(holder -> holder instanceof ActionBarHolder && !notificationStyles.contains("ACTIONBAR"));
            holders.removeIf(holder -> holder instanceof TitleHolder && !notificationStyles.contains("TITLE"));
            holders.removeIf(holder -> holder instanceof BossBarHolder && !notificationStyles.contains("BOSSBAR"));

            if (holders.isEmpty()) {
                return false;
            }

            Configurer configurer = config.getConfigurer();
            Object value = configurer.simplify(
                    SendableMessage.of(holders),
                    GenericsDeclaration.of(SendableMessage.class),
                    SerdesContext.of(configurer),
                    true
            );
            view.set(FunnyFormatter.format(key, "{TYPE}", ""), value);

            return true;
        };
    }

    @Nullable
    private static ChatHolder prepareChatNotification(RawConfigView view, String key) {
        if (!view.exists(key)) {
            return null;
        }
        return new ChatHolder(false, getRawComponent(view, key));
    }

    @Nullable
    private static ActionBarHolder prepareActionBarNotification(RawConfigView view, String key) {
        if (!view.exists(key)) {
            return null;
        }
        return new ActionBarHolder(getRawComponent(view, key));
    }

    @Nullable
    private static TitleHolder prepareTitleNotification(RawConfigView view, String key) {
        if (!view.exists(key)) {
            return null;
        }

        TitleHolder.Builder builder = TitleHolder.builder();
        builder.times(
                (Integer) getConfigValueOrDefault("notification-title-fade-in", 10),
                (Integer) getConfigValueOrDefault("notification-title-stay", 10),
                (Integer) getConfigValueOrDefault("notification-title-fade-out", 10)
        );

        RawComponent title = getRawComponent(view, key);
        if (title != null) {
            builder.title(title);
        }

        RawComponent subTitle = getRawComponent(view, FunnyFormatter.format(key, "Title", "Subtitle"));
        if (subTitle != null) {
            builder.subTitle(subTitle);
        }

        return builder.build();
    }

    @Nullable
    private static BossBarHolder prepareBossBarNotification(RawConfigView view, String key) {
        if (!view.exists(key)) {
            return null;
        }

        BossBarHolder.Builder builder = BossBarHolder.builder(getRawComponent(view, key));
        builder.clearOtherBars(true);

        String color = (String) getConfigValueOrDefault("notification-boss-bar-color", "PINK");
        if (color != null) {
            builder.color(BossBar.Color.valueOf(color.toUpperCase()));
        }

        String style = (String) getConfigValueOrDefault("notification-boss-bar-style", "SOLID");
        if (style != null) {
            builder.overlay(getOverlay(style));
        }

        ((List<String>) getConfigValueOrDefault("notification-boss-bar-flags", new ArrayList<>()))
                .stream()
                .map(M0001_Migrate_old_region_notification_keys::getFlag)
                .filter(Objects::nonNull)
                .forEach(builder::addFlag);

        return builder.build();
    }

    private static BossBar.Overlay getOverlay(String legacyStyle) {
        BossBar.Overlay overlay;
        switch (legacyStyle.toUpperCase()) {
            case "SOLID":
                overlay = BossBar.Overlay.PROGRESS;
                break;
            case "SEGMENTED_6":
                overlay = BossBar.Overlay.NOTCHED_6;
                break;
            case "SEGMENTED_10":
                overlay = BossBar.Overlay.NOTCHED_10;
                break;
            case "SEGMENTED_12":
                overlay = BossBar.Overlay.NOTCHED_12;
                break;
            case "SEGMENTED_20":
                overlay = BossBar.Overlay.NOTCHED_20;
                break;
            default:
                overlay = BossBar.Overlay.PROGRESS;
        }
        return overlay;
    }

    @Nullable
    private static BossBar.Flag getFlag(String legacyFlag) {
        BossBar.Flag flag;
        switch (legacyFlag.toUpperCase()) {
            case "DARKEN_SKY":
                flag = BossBar.Flag.DARKEN_SCREEN;
                break;
            case "PLAY_BOSS_MUSIC":
                flag = BossBar.Flag.PLAY_BOSS_MUSIC;
                break;
            case "CREATE_FOG":
                flag = BossBar.Flag.CREATE_WORLD_FOG;
                break;
            default:
                flag = null;
        }
        return flag;
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
