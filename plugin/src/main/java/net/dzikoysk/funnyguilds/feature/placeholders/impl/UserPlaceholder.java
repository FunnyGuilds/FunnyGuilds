package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import net.dzikoysk.funnyguilds.feature.placeholders.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.UserResolver;
import net.dzikoysk.funnyguilds.user.User;

public class UserPlaceholder implements Placeholder<User> {

    private final UserResolver userResolver;
    private final UserResolver fallBackResolver;

    public UserPlaceholder(UserResolver userResolver, UserResolver fallBackResolver) {
        this.userResolver = userResolver;
        this.fallBackResolver = fallBackResolver;
    }

    public UserPlaceholder(UserResolver userResolver) {
        this(userResolver, user -> "Brak");
    }

    @Override
    public Object getRaw(User user) {
        return this.userResolver.resolve(user);
    }

    @Override
    public Object getRawFallback(User user) {
        return this.fallBackResolver.resolve(user);
    }
}
