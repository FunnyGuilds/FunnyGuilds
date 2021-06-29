package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.resources.Validator;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.panda_lang.utilities.inject.Property;

@FunnyComponent
final class MemberValidator implements Validator<IsMember, User, ValidationException> {

    @Override
    public boolean validate(Context context, IsMember annotation, Property property, User user) throws ValidationException {
        return isMember(user);
    }

    boolean isMember(User user) {
        if (!user.hasGuild()) {
            throw new ValidationException(FunnyGuilds.getInstance().getMessageConfiguration().generalHasNoGuild);
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
