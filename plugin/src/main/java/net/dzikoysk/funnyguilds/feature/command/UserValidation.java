package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.user.User;

public final class UserValidation {

    private UserValidation() {
    }

    public static User requireUserByName(String name) {
        return FunnyGuilds.getInstance().getUserManager().findByName(name, true).orElseThrow(() -> {
            return new InternalValidationException(config -> config.generalNotPlayedBefore);
        });
    }

}
