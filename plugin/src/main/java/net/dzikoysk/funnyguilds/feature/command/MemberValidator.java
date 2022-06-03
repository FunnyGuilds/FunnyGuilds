package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.resources.Validator;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import org.panda_lang.utilities.inject.Property;

@FunnyComponent
final class MemberValidator implements Validator<IsMember, User, ValidationException> {

    private final MessageConfiguration messageConfiguration;

    MemberValidator(MessageConfiguration messageConfiguration) {
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public boolean validate(Context context, IsMember annotation, Property property, User user) throws ValidationException {
        return this.isMember(user);
    }

    boolean isMember(User user) {
        if (!user.hasGuild()) {
            throw new ValidationException(this.messageConfiguration.generalHasNoGuild);
        }

        return true;
    }

    @Override
    public Class<IsMember> getAnnotation() {
        return IsMember.class;
    }

    @Override
    public Class<User> getType() {
        return User.class;
    }

}
