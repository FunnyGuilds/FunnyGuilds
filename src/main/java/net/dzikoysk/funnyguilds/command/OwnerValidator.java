package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.resources.Validator;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.panda_lang.utilities.inject.InjectorProperty;

@FunnyComponent
final class OwnerValidator implements Validator<IsOwner, User, ValidationException> {

    private static final MemberValidator MEMBER_VALIDATOR = new MemberValidator();

    @Override
    public boolean validate(Context context, IsOwner annotation, InjectorProperty property, User user) throws ValidationException {
        MEMBER_VALIDATOR.isMember(user);

        if (!user.isOwner()) {
            throw new ValidationException(FunnyGuilds.getInstance().getMessageConfiguration().generalIsNotOwner);
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
