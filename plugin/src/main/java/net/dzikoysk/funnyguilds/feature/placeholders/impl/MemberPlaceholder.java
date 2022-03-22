package net.dzikoysk.funnyguilds.feature.placeholders.impl;

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

    @Override
    public Object getRaw(User user) {
        return user.getGuild()
                .map(guild -> this.whenInGuild.resolve(user, guild))
                .orElseGet(this.getRawFallback(user));
    }

    @Override
    public Object getRawFallback(User user) {
        return this.whenNotInGuild.resolve(user);
    }

}

