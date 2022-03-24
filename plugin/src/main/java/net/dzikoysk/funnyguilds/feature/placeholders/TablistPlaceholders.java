package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.LocalDateTime;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.UserPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.PairResolver;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

public class TablistPlaceholders extends Placeholders<User, UserPlaceholder> {

    public static final TablistPlaceholders TABLIST;

    static {
        TABLIST = new TablistPlaceholders();

        Placeholders.TIME.getPlaceholders().forEach((name, placeholder) ->
                TABLIST.raw(name, new UserPlaceholder(user -> placeholder.getRaw(LocalDateTime.now()))));

        Placeholders.SIMPLE.getPlaceholders().forEach((name, placeholder) ->
                TABLIST.raw(name, new UserPlaceholder(user -> placeholder.getRaw(null))));

        UserPlaceholders.USER.getPlaceholders().forEach(TABLIST::raw);
        UserPlaceholders.PLAYER.getPlaceholders().forEach(TABLIST::raw);

        GuildPlaceholders.GUILD_ALL.getPlaceholders().forEach((name, placeholder) ->
                TABLIST.raw("{G-" + (name.replace("{", "")),
                        (user, guild) -> placeholder.getRaw(guild),
                        user -> placeholder.getRawFallback(null)
                ));
    }

    protected TablistPlaceholders raw(String name, PairResolver<User, Guild> whenInGuild, MonoResolver<User> whenNotInGuild) {
        this.raw(name, new UserPlaceholder(user -> user.getGuild()
                .map(guild -> whenInGuild.resolve(user, guild))
                .orElseGet(whenNotInGuild.resolve(user))));
        return this;
    }

}
