package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import net.dzikoysk.funnyguilds.feature.placeholders.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.UserResolver;
import net.dzikoysk.funnyguilds.user.User;

public class UserPlaceholder implements Placeholder<User> {

    private final UserResolver userResolver;

    public UserPlaceholder(UserResolver userResolver) {
        this.userResolver = userResolver;
    }

    @Override
    public Object getRaw(User user) {
        return this.userResolver.resolve(user);
    }

}
