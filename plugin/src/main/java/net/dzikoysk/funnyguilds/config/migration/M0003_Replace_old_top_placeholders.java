package net.dzikoysk.funnyguilds.config.migration;

import dev.peri.yetanothermessageslibrary.message.SendableMessage;
import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.builtin.NamedMigration;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.stream.PandaStream;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.update;

public class M0003_Replace_old_top_placeholders extends NamedMigration {

    private final static Replaceable LEGACY_TO_MODERN_TOP_REPLACEMENT = new Replaceable() {
        @Override
        public @NotNull String replace(@Nullable Locale locale, @NotNull String text) {
            // Simple solution, but it works
            return text
                    .replace("{PTOP-", "{PTOP-POINTS-")
                    .replace("{GTOP-", "{GTOP-AVG_POINTS-");
        }

        @Override
        public @NotNull Component replace(@Nullable Locale locale, @NotNull Component text) {
            return text
                    .replaceText(builder -> {
                        builder.match("{PTOP-").replacement("{PTOP-POINTS-");
                    })
                    .replaceText(builder -> {
                        builder.match("{GTOP-").replacement("{GTOP-AVG_POINTS-");
                    });
        }
    };

    public M0003_Replace_old_top_placeholders() {
        super(
                "Replace old top placeholders {P/GTOP-x} to new format {P/GTOP-type-x}",
                replaceLegacyPlaceholders("rankingList"),
                replaceLegacyPlaceholders("topList")
        );
    }

    private static ConfigMigration replaceLegacyPlaceholders(String key) {
        return update(key, value -> {
            SendableMessage message = (SendableMessage) value;
            return SendableMessage.of(PandaStream.of(message.getHolders())
                    .map(holder -> holder.copy(LEGACY_TO_MODERN_TOP_REPLACEMENT))
                    .toList());
        });
    }

}
