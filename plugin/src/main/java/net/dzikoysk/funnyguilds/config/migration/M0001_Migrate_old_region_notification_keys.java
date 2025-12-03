package net.dzikoysk.funnyguilds.config.migration;

import dev.peri.yetanothermessageslibrary.adventure.MiniComponent;
import dev.peri.yetanothermessageslibrary.adventure.RawComponent;
import dev.peri.yetanothermessageslibrary.message.SendableMessage;
import dev.peri.yetanothermessageslibrary.message.holder.SendableHolder;
import dev.peri.yetanothermessageslibrary.message.holder.impl.ActionBarHolder;
import dev.peri.yetanothermessageslibrary.message.holder.impl.BossBarHolder;
import dev.peri.yetanothermessageslibrary.message.holder.impl.ChatHolder;
import dev.peri.yetanothermessageslibrary.message.holder.impl.TitleHolder;
import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.view.RawConfigView;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: [5.0] Remove this madness
public class M0001_Migrate_old_region_notification_keys extends FunnyMigration {

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

            List<String> notificationStyles = plugin().getOr("region-move-notification-style", List.class, new ArrayList<>());
            holders.removeIf(holder -> holder instanceof ChatHolder && !notificationStyles.contains("CHAT"));
            holders.removeIf(holder -> holder instanceof ActionBarHolder && !notificationStyles.contains("ACTIONBAR"));
            holders.removeIf(holder -> holder instanceof TitleHolder && !notificationStyles.contains("TITLE"));
            holders.removeIf(holder -> holder instanceof BossBarHolder && !notificationStyles.contains("BOSSBAR"));

            if (holders.isEmpty()) {
                return false;
            }

            view.set(
                FunnyFormatter.format(key, "{TYPE}", ""),
                SendableMessage.of(holders)
            );

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

        RawConfigView plugin = plugin();
        TitleHolder.Builder builder = TitleHolder.builder();
        builder.times(
            plugin.getOr("notification-title-fade-in", Integer.class, 10),
            plugin.getOr("notification-title-stay", Integer.class, 10),
            plugin.getOr("notification-title-fade-out", Integer.class, 10)
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

        RawConfigView plugin = plugin();
        BossBarHolder.Builder builder = BossBarHolder.builder(getRawComponent(view, key));
        builder.clearOtherBars(true);

        Duration stay = plugin.getOr("region-notification-time", Duration.class, Duration.ofSeconds(15));
        builder.stay((int) stay.toMillis() / 50);

        String color = plugin.getOr("notification-boss-bar-color", String.class, "PINK");
        builder.color(BossBar.Color.valueOf(color.toUpperCase()));

        String style = plugin.getOr("notification-boss-bar-style", String.class, "SOLID");
        builder.overlay(getOverlay(style));

        List<String> flags = plugin.getOr("notification-boss-bar-flags", List.class, new ArrayList<String>());
        flags.stream()
                .map(M0001_Migrate_old_region_notification_keys::getFlag)
                .filter(Objects::nonNull)
                .forEach(builder::addFlag);

        return builder.build();
    }

    private static BossBar.Overlay getOverlay(String legacyStyle) {
        return switch (legacyStyle.toUpperCase()) {
            case "SOLID" -> BossBar.Overlay.PROGRESS;
            case "SEGMENTED_6" -> BossBar.Overlay.NOTCHED_6;
            case "SEGMENTED_10" -> BossBar.Overlay.NOTCHED_10;
            case "SEGMENTED_12" -> BossBar.Overlay.NOTCHED_12;
            case "SEGMENTED_20" -> BossBar.Overlay.NOTCHED_20;
            default -> BossBar.Overlay.PROGRESS;
        };
    }

    @Nullable
    private static BossBar.Flag getFlag(String legacyFlag) {
        return switch (legacyFlag.toUpperCase()) {
            case "DARKEN_SKY" -> BossBar.Flag.DARKEN_SCREEN;
            case "PLAY_BOSS_MUSIC" -> BossBar.Flag.PLAY_BOSS_MUSIC;
            case "CREATE_FOG" -> BossBar.Flag.CREATE_WORLD_FOG;
            default -> null;
        };
    }

    @Nullable
    private static RawComponent getRawComponent(RawConfigView view, String key) {
        if (!view.exists(key)) {
            return null;
        }
        String message = (String) view.remove(key);
        return MiniComponent.of(message);
    }

}
