package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.resources.Validator;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.panda_lang.utilities.inject.InjectorProperty;

@FunnyComponent
final class OwnerValidator implements Validator<IsOwner, User, ValidationException> {

    @Override
    public boolean validate(Origin origin, IsOwner annotation, InjectorProperty property, User user) throws ValidationException {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        if (!user.hasGuild()) {
            throw new ValidationException(messages.generalHasNoGuild);
        }

        if (!user.isOwner()) {
            throw new ValidationException(messages.generalIsNotOwner);
        }

        return true;
    }

}
