package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import net.dzikoysk.funnyguilds.user.User;

@FunctionalInterface
public interface UserResolver {
    Object resolve(User user);
}
