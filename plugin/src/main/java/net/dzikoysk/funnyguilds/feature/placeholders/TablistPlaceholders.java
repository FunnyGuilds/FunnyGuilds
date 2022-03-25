package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.LocalDateTime;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.FallbackPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.PairResolver;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

public class TablistPlaceholders extends Placeholders<User> {

    public static final TablistPlaceholders TABLIST;

    static {
        TABLIST = new TablistPlaceholders();

        Placeholders.SIMPLE.getPlaceholders().forEach((name, placeholder) ->
                TABLIST.raw(name, new Placeholder<>(user -> placeholder.getRaw(null))));

        Placeholders.TIME.getPlaceholders().forEach((name, placeholder) ->
                TABLIST.raw(name, new Placeholder<>(user -> placeholder.getRaw(LocalDateTime.now()))));

        UserPlaceholders.USER.getPlaceholders().forEach(TABLIST::raw);
        UserPlaceholders.PLAYER.getPlaceholders().forEach(TABLIST::raw);

        GuildPlaceholders.GUILD_ALL.getPlaceholders().forEach((name, placeholder) ->
                TABLIST.raw("{G-" + (name.replace("{", "")),
                        (user, guild) -> placeholder.getRaw(guild),
                        user -> placeholder instanceof FallbackPlaceholder
                                ? ((FallbackPlaceholder<?>) placeholder).getRawFallback()
                                : ""
                ));
    }

    public TablistPlaceholders raw(String name, PairResolver<User, Guild> whenInGuild, MonoResolver<User> whenNotInGuild) {
        TablistPlaceholders copy = new TablistPlaceholders();
        copy.placeholders.putAll(this.placeholders);
        copy.raw(name, new Placeholder<>(user -> user.getGuild()
                .map(guild -> whenInGuild.resolve(user, guild))
                .orElseGet(whenNotInGuild.resolve(user))));
        return copy;
    }

}
