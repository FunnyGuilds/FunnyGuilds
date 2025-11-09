package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.resources.Validator;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionChecker;
import net.dzikoysk.funnyguilds.user.User;
import org.panda_lang.utilities.inject.Property;

@FunnyComponent
final class HasGuildPermissionValidator implements Validator<HasGuildPermission, User, ValidationException> {

    private final GuildPermissionChecker permissionChecker;

    HasGuildPermissionValidator(GuildPermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

    @Override
    public boolean validate(Context context, HasGuildPermission annotation, Property property, User user) throws ValidationException {
        Guild guild = user
                .getGuild()
                .orThrow(() -> new InternalValidationException(config -> config.generalHasNoGuild));
        return this.permissionChecker.handlePermission(guild, user, annotation.value());
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
