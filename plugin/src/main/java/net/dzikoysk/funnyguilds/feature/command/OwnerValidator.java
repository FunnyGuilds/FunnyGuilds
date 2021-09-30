package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.resources.Validator;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import org.panda_lang.utilities.inject.Property;

@FunnyComponent
final class OwnerValidator implements Validator<IsOwner, User, ValidationException> {

    private final MessageConfiguration messageConfiguration;
    private final MemberValidator memberValidator;

    OwnerValidator(MessageConfiguration messageConfiguration) {
        this.messageConfiguration = messageConfiguration;
        this.memberValidator = new MemberValidator(messageConfiguration);
    }

    @Override
    public boolean validate(Context context, IsOwner annotation, Property property, User user) throws ValidationException {
        this.memberValidator.isMember(user);

        if (!user.isOwner()) {
            throw new ValidationException(this.messageConfiguration.generalIsNotOwner);
        }

        return true;
    }

    @Override
    public Class<IsOwner> getAnnotation() {
        return IsOwner.class;
    }

    @Override
    public Class<User> getType() {
        return User.class;
    }

}
