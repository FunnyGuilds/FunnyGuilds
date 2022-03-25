package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.FallbackPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.Placeholder;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.stream.PandaStream;

public class TablistPlaceholders extends Placeholders<User> {

    public static final TablistPlaceholders TABLIST;

    static {
        TABLIST = new TablistPlaceholders()
                .raw(PandaStream.of(Placeholders.SIMPLE.getPlaceholders().entrySet())
                        .toMap(Entry::getKey, entry -> new Placeholder<>(user -> entry.getValue().getRaw(null))))
                .raw(PandaStream.of(Placeholders.TIME.getPlaceholders().entrySet())
                        .toMap(Entry::getKey, entry -> new Placeholder<>(user -> entry.getValue().getRaw(LocalDateTime.now()))))
                .raw(UserPlaceholders.USER.getPlaceholders())
                .raw(UserPlaceholders.PLAYER.getPlaceholders())
                .raw(PandaStream.of(GuildPlaceholders.GUILD_ALL.getPlaceholders().entrySet())
                        .toMap(entry -> "{G-" + (entry.getKey().replace("{", "")),
                                entry -> {
                                    Placeholder<Guild> placeholder = entry.getValue();
                                    return new Placeholder<>(user -> user.getGuild()
                                            .map(placeholder::getRaw)
                                            .orElseGet(() -> placeholder instanceof FallbackPlaceholder
                                                    ? ((FallbackPlaceholder<?>) placeholder).getRawFallback()
                                                    : ""));
                                }));
    }

    public TablistPlaceholders raw(Map<String, Placeholder<User>> placeholders) {
        TablistPlaceholders copy = new TablistPlaceholders();
        copy.placeholders.putAll(this.placeholders);
        copy.placeholders.putAll(placeholders);
        return copy;
    }

}
