package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;

public final class UserValidation {

    private UserValidation() {}

    public static User requireUserByName(String name) {
        User user = FunnyGuilds.getInstance().getUserManager().getUser(name, true);

        if (user == null) {
            throw new ValidationException(FunnyGuilds.getInstance().getMessageConfiguration().generalNotPlayedBefore);
        }

        return user;
    }

}
