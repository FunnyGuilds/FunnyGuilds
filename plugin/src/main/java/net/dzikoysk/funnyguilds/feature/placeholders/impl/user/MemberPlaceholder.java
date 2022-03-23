package net.dzikoysk.funnyguilds.feature.placeholders.impl.user;

import net.dzikoysk.funnyguilds.feature.placeholders.impl.FallbackPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MemberResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.UserResolver;
import net.dzikoysk.funnyguilds.user.User;

public class MemberPlaceholder extends UserPlaceholder implements FallbackPlaceholder<User> {

    private final MemberResolver whenInGuild;
    private final UserResolver whenNotInGuild;

    public MemberPlaceholder(MemberResolver whenInGuild, UserResolver whenNotInGuild) {
        super(whenNotInGuild);
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
        if (user == null) {
            return null;
        }
        return this.whenNotInGuild.resolve(user);
    }

}

