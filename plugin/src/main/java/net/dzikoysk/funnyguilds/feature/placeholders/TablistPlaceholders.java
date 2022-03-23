package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.LocalDateTime;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.user.MemberPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.user.UserPlaceholder;
import net.dzikoysk.funnyguilds.user.User;

public class TablistPlaceholders extends Placeholders<User, UserPlaceholder> {

    public static final Placeholders<User, UserPlaceholder> TABLIST;

    static {
        TABLIST = new TablistPlaceholders();

        Placeholders.TIME.getPlaceholders().forEach((name, placeholder) ->
                TABLIST.raw(name, new UserPlaceholder(user -> placeholder.getRaw(LocalDateTime.now()))));

        Placeholders.SIMPLE.getPlaceholders().forEach((name, placeholder) ->
                TABLIST.raw(name, new UserPlaceholder(user -> placeholder.getRaw(null))));

        PlayerPlaceholders.PLAYER.getPlaceholders().forEach(TABLIST::raw);
        UserPlaceholders.USER.getPlaceholders().forEach(TABLIST::raw);

        GuildPlaceholders.GUILD_ALL.getPlaceholders().forEach((name, placeholder) ->
                TABLIST.raw("{G-" + (name.replace("{", "")),
                        new MemberPlaceholder(
                                (user, guild) -> placeholder.getRaw(guild),
                                user -> placeholder.getRawFallback(null)
                        )
                ));
    }

}
