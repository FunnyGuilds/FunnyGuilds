package net.dzikoysk.funnyguilds.guild.permission;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import panda.std.Result;

/**
 * A static implementation of GuildPermissionController that defines fixed permissions based on user roles within a
 * guild.
 * It preserves the original permission checking logic while providing a straightforward permission structure.
 */
final class StaticGuildPermissionController implements GuildPermissionController {

    private static final Collection<? extends GuildPermission<?>> MEMBER_PERMISSIONS = EnumSet.of(
            GuildCommandPermission.BASE,
            GuildCommandPermission.LEAVE
    );
    private static final Collection<? extends GuildPermission<?>> MANAGER_PERMISSIONS = EnumSet.of(
            GuildCommandPermission.SET_BASE,
            GuildCommandPermission.ENLARGE,
            GuildCommandPermission.EXTEND_VALIDITY,
            GuildCommandPermission.INVITE,
            GuildCommandPermission.KICK,
            GuildCommandPermission.PVP
    );
    private static final Collection<? extends GuildPermission<?>> OWNER_PERMISSIONS = EnumSet.of(
            GuildCommandPermission.DEPUTY,
            GuildCommandPermission.ALLY,
            GuildCommandPermission.WAR,
            GuildCommandPermission.DELETE
    );

    private final PluginConfiguration pluginConfiguration;
    private final MessageService messageService;

    StaticGuildPermissionController(
            PluginConfiguration pluginConfiguration,
            MessageService messageService
    ) {
        this.pluginConfiguration = pluginConfiguration;
        this.messageService = messageService;
    }
    
    @Override
    public <T> Result<T, Runnable> getPermissionResult(
            Guild guild,
            User user,
            GuildPermission<T> permission
    ) {
        if (!guild.isMember(user)) {
            return Result.error(this.errorMessageAction(
                    user,
                    config -> config.generalIsNotMember
            ));
        }

        return this.getPermissionResultInternal(guild, user, permission)
                .mapErr(errorAction -> {
                    if (errorAction != null) {
                        return errorAction;
                    }
                    return this.errorMessageAction(
                            user,
                            config -> config.generalInsufficientGuildPermission
                    );
                })
                .map(permission.getValueType()::cast);
    }
    
    private Result<?, Runnable> getPermissionResultInternal(
            Guild guild,
            User user,
            GuildPermission<?> permission
    ) {
        if (MEMBER_PERMISSIONS.contains(permission)) {
            return this.handlePermission(
                    guild.isMember(user),
                    user,
                    config -> config.generalIsNotMember
            );
        }
        else if (MANAGER_PERMISSIONS.contains(permission)) {
            return this.handlePermission(
                    guild.isOwner(user) || guild.isDeputy(user),
                    user,
                    config -> config.generalInsufficientGuildPermission
            );
        }
        else if (OWNER_PERMISSIONS.contains(permission)) {
            return this.handlePermission(
                    guild.isOwner(user),
                    user,
                    config -> config.generalIsNotOwner
            );
        }
        else if (GenericGuildPermissions.USER_POSITION.equals(permission)) {
            return this.getGuildUserPositionValue(user);
        }
        else if (GenericGuildPermissions.CHAT_PERMISSIONS.contains(permission)) {
            return Result.ok(true);
        }

        return Result.error(null);
    }
    
    private Result<String, Runnable> getGuildUserPositionValue(User user) {
        String value;
        if (user.isOwner()) {
            value = this.pluginConfiguration.chatPositionLeader.getValue();
        }
        else if (user.isDeputy()) {
            value = this.pluginConfiguration.chatPositionDeputy.getValue();
        }
        else {
            value = this.pluginConfiguration.chatPositionMember.getValue();
        }
        return Result.ok(value);
    }

    @Override
    public Result<Boolean, Runnable> getProtectionPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission,
            @Nullable Event event
    ) {
        return this.handlePermission(
                guild.isMember(user),
                user,
                config -> config.regionUnauthorized
        );
    }

    private Result<Boolean, Runnable> handlePermission(
            boolean hasSufficientPermission,
            User user,
            Function<MessageConfiguration, Sendable> messageSupplier
    ) {
        return Result.when(
                hasSufficientPermission,
                () -> true,
                () -> this.errorMessageAction(user, messageSupplier)
        );
    }
    
    private Runnable errorMessageAction(
            User user,
            Function<MessageConfiguration, Sendable> messageSupplier
    ) {
        return () -> this.messageService.getMessage(messageSupplier)
                .receiver(user)
                .send();
    }
}
