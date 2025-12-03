package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.view.RawConfigView;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import org.jetbrains.annotations.Nullable;
import dev.peri.yetanothermessageslibrary.adventure.MiniComponent;
import dev.peri.yetanothermessageslibrary.adventure.RawComponent;
import dev.peri.yetanothermessageslibrary.message.SendableMessage;
import dev.peri.yetanothermessageslibrary.message.holder.impl.TitleHolder;

public class M0002_Migrate_old_rank_kill_message extends FunnyMigration {

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

            RawConfigView plugin = plugin();
            TitleHolder.Builder builder = TitleHolder.builder();
            builder.times(
                    plugin.getOr("notification-title-fade-in", Integer.class, 10),
                    plugin.getOr("notification-title-stay", Integer.class, 10),
                    plugin.getOr("notification-title-fade-out", Integer.class, 10)
            );

            RawComponent title = getRawComponent(view, oldKey);
            if (title != null) {
                builder.title(title);
            }

            RawComponent subTitle = getRawComponent(view, FunnyFormatter.format(oldKey, "Title", "Subtitle"));
            if (subTitle != null) {
                builder.subTitle(subTitle);
            }

            view.set(newKey, SendableMessage.of(builder.build()));
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

}
