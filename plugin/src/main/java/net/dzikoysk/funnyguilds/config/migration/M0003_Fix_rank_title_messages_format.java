package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.migrate.builtin.NamedMigration;
import eu.okaeri.configs.migrate.view.RawConfigView;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.SerdesContext;
import dev.peri.yetanothermessageslibrary.message.SendableMessage;
import dev.peri.yetanothermessageslibrary.message.holder.impl.TitleHolder;

public class M0003_Fix_rank_title_messages_format extends NamedMigration {

    public M0003_Fix_rank_title_messages_format() {
        super(
                "Fix rank title messages format to use proper TitleHolder serialization",
                fixTitleMessage("rankKillMessage", "&cZabiles gracza {VICTIM}", "&7{PLUS-FORMATTED}"),
                fixTitleMessage("rankDeathVictimMessage", "&cZostales zabity przez {ATTACKER}", "&7{MINUS-FORMATTED}"),
                fixTitleMessage("rankDeathAssistMessage", "&aAsysta przy zabiciu {VICTIM}", "&7{PLUS-FORMATTED}")
        );
    }

    private static ConfigMigration fixTitleMessage(String key, String defaultTitle, String defaultSubtitle) {
        return ((config, view) -> {
            if (!view.exists(key)) {
                return false;
            }

            // Check if the message is already in the correct format by checking if it has "holders" key
            Object currentValue = view.get(key);
            if (currentValue instanceof java.util.Map) {
                java.util.Map<?, ?> map = (java.util.Map<?, ?>) currentValue;
                // If it already has holders, it's in the correct format
                if (map.containsKey("holders") && map.get("holders") instanceof java.util.List) {
                    java.util.List<?> holders = (java.util.List<?>) map.get("holders");
                    // Check if there's at least one holder that looks like a TitleHolder
                    for (Object holder : holders) {
                        if (holder instanceof java.util.Map) {
                            java.util.Map<?, ?> holderMap = (java.util.Map<?, ?>) holder;
                            if ("TITLE".equals(holderMap.get("type"))) {
                                // Already in correct format, skip migration
                                return false;
                            }
                        }
                    }
                }
            }

            // Remove the old value
            view.remove(key);

            // Create new TitleHolder with builder pattern
            TitleHolder.Builder builder = TitleHolder.builder();
            builder.title(defaultTitle);
            builder.subTitle(defaultSubtitle);
            builder.times(10, 10, 10);

            // Serialize the new message
            Configurer configurer = config.getConfigurer();
            Object value = configurer.simplify(
                    SendableMessage.of(builder.build()),
                    GenericsDeclaration.of(SendableMessage.class),
                    SerdesContext.of(configurer),
                    true
            );
            view.set(key, value);
            return true;
        });
    }

}
