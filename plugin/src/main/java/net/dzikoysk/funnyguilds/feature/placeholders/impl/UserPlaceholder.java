package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.user.User;

public class UserPlaceholder implements Placeholder<User> {

    private final MonoResolver<User> userResolver;

    public UserPlaceholder(MonoResolver<User> userResolver) {
        this.userResolver = userResolver;
    }

    @Override
    public Object getRaw(User user) {
        return this.userResolver.resolve(user);
    }

}
