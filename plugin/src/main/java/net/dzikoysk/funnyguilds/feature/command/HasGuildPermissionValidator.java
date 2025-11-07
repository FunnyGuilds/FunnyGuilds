package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.resources.Validator;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionController;
import net.dzikoysk.funnyguilds.user.User;
import org.panda_lang.utilities.inject.Property;

@FunnyComponent
final class HasGuildPermissionValidator implements Validator<HasGuildPermission, User, ValidationException> {

    private final GuildPermissionController permissionController;

    HasGuildPermissionValidator(GuildPermissionController permissionController) {
        this.permissionController = permissionController;
    }

    @Override
    public boolean validate(Context context, HasGuildPermission annotation, Property property, User user) throws ValidationException {
        if (!user.hasGuild()) {
            throw new InternalValidationException(config -> config.generalHasNoGuild);
        }

        GuildCommandPermission permission = annotation.value();
        if (!this.permissionController.canPerformAction(user, permission)) {
            // TODO: customizable message
            throw new InternalValidationException(config -> config.generalDoesNotHasPermission);
        }

        return true;
    }

    @Override
    public Class<HasGuildPermission> getAnnotation() {
        return HasGuildPermission.class;
    }

    @Override
    public Class<User> getType() {
        return User.class;
    }

}
