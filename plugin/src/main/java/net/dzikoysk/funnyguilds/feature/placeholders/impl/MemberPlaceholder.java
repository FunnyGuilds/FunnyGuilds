package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import java.util.Objects;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MemberResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.UserResolver;
import net.dzikoysk.funnyguilds.user.User;

public class MemberPlaceholder implements Placeholder<User> {

    private final MemberResolver whenInGuild;
    private final UserResolver whenNotInGuild;

    public MemberPlaceholder(MemberResolver whenInGuild, UserResolver whenNotInGuild) {
        this.whenInGuild = whenInGuild;
        this.whenNotInGuild = whenNotInGuild;
    }

    public MemberPlaceholder(MemberResolver whenInGuild, String whenNotInGuild) {
        this(whenInGuild, (user) -> whenNotInGuild);
    }

    @Override
    public String getRaw(User user) {
        return Objects.toString(user.getGuild()
                .map(guild -> this.whenInGuild.resolve(user, guild))
                .orElseGet(this.whenNotInGuild.resolve(user)));
    }

}
