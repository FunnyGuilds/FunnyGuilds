package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.user.User;

public final class UserValidation {

    private UserValidation() {}

    public static User requireUserByName(String name) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();

        return plugin.getUserManager().findByName(name, true)
                .orThrow(() -> new ValidationException(plugin.getMessageConfiguration().generalNotPlayedBefore));
    }

}
