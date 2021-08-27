package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public final class UserValidation {

    private UserValidation() {}

    public static User requireUserByName(String name) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        Option<User> user = plugin.getUserManager().getUser(name, true);

        if (user.isEmpty()) {
            throw new ValidationException(plugin.getMessageConfiguration().generalNotPlayedBefore);
        }

        return user.get();
    }

}
