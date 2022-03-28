package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.FallbackPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.Placeholder;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.stream.PandaStream;

public class TablistPlaceholders extends Placeholders<User, TablistPlaceholders> {

    public static final TablistPlaceholders TABLIST;

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();

        TABLIST = new TablistPlaceholders()
                .raw(mapPlaceholders(SimplePlaceholders.SIMPLE.getPlaceholders(), () -> null))
                .raw(mapPlaceholders(SimplePlaceholders.TIME.getPlaceholders(),
                        () -> OffsetDateTime.now().plusHours(config.timeOffset)))
                .raw(UserPlaceholders.USER.getPlaceholders())
                .raw(UserPlaceholders.PLAYER.getPlaceholders())
                .raw(PandaStream.of(GuildPlaceholders.GUILD_ALL.getPlaceholders().entrySet())
                        .toMap(entry -> "{G-" + (entry.getKey().substring(1)),
                                entry -> {
                                    Placeholder<Guild> placeholder = entry.getValue();
                                    return new Placeholder<>(user -> user.getGuild()
                                            .map(placeholder::getRaw)
                                            .orElseGet(() -> placeholder instanceof FallbackPlaceholder
                                                    ? ((FallbackPlaceholder<?>) placeholder).getRawFallback()
                                                    : ""));
                                }));
    }

    @Override
    public TablistPlaceholders create() {
        return new TablistPlaceholders();
    }

    private static <T> Map<String, Placeholder<User>> mapPlaceholders(Map<String, Placeholder<T>> placeholders, Supplier<T> dataSupplier) {
        return PandaStream.of(placeholders.entrySet())
                .toMap(Entry::getKey, entry -> new Placeholder<>(user -> entry.getValue().getRaw(dataSupplier.get())));
    }

}