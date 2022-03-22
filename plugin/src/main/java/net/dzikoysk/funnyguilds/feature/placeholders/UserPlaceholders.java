package net.dzikoysk.funnyguilds.feature.placeholders;

import net.dzikoysk.funnyguilds.feature.placeholders.impl.UserPlaceholder;
import net.dzikoysk.funnyguilds.user.User;

public class UserPlaceholders extends Placeholders<User, UserPlaceholder> {

    public static final Placeholders<User, UserPlaceholder> USER_PLACEHOLDERS;

    static {
        USER_PLACEHOLDERS = new UserPlaceholders()
                .register("name", new UserPlaceholder(User::getName));
    }

}
