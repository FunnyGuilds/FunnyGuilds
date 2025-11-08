package net.dzikoysk.funnyguilds.guild.permission;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import panda.std.Result;

/**
 * A static implementation of GuildPermissionController that defines fixed permissions based on user roles within a 
 * guild.
 * It preserves the original permission checking logic while providing a straightforward permission structure.
 */
final class StaticGuildPermissionController implements GuildPermissionController {

    private static final Collection<GuildCommandPermission> MEMBER_PERMISSIONS = EnumSet.of(
            GuildCommandPermission.BASE,
            GuildCommandPermission.LEAVE
    );
    private static final Collection<GuildCommandPermission> MANAGER_PERMISSIONS = EnumSet.of(
            GuildCommandPermission.SET_BASE,
            GuildCommandPermission.ENLARGE,
            GuildCommandPermission.EXTEND_VALIDITY,
            GuildCommandPermission.INVITE,
            GuildCommandPermission.KICK,
            GuildCommandPermission.PVP
    );
    private static final Collection<GuildCommandPermission> OWNER_PERMISSIONS = EnumSet.of(
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> Option<T> getPermissionValue(
            Guild guild,
            User user,
            GuildPermission<T> permission
    ) {
        if (permission.getValueType() == Boolean.class) {
            return this.getPermissionResult(
                            guild,
                            user,
                            (GuildPermission<Boolean>) permission
                    )
                    .map(result -> (T) result)
                    .toOption();
        }

        if (GenericGuildPermissions.USER_POSITION.equals(permission)) {
            return this.getGuildUserPositionValue(user)
                    .map(value -> (T) value);
        }

        return Option.none();
    }

    private Option<String> getGuildUserPositionValue(User user) {
        String value = "";
        if (user.isOwner()) {
            value = this.pluginConfiguration.chatPositionLeader.getValue();
        }
        else if (user.isDeputy()) {
            value = this.pluginConfiguration.chatPositionDeputy.getValue();
        }
        else {
            value = this.pluginConfiguration.chatPositionMember.getValue();
        }
        return Option.of(value);
    }

    @Override
    public Result<Boolean, Runnable> getPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission
    ) {
        if (!guild.isMember(user)) {
            return Result.error(() -> this.messageService.getMessage(config -> config.generalIsNotMember)
                    .receiver(user)
                    .send());
        }

        if (permission instanceof GuildCommandPermission) {
            return this.handleCommandPermissions(
                    guild,
                    user,
                    (GuildCommandPermission) permission
            );
        }
        else if (permission instanceof GuildProtectionPermission) {
            return this.getProtectionPermissionResult(
                    guild,
                    user,
                    permission,
                    null
            );
        }
        else if (GenericGuildPermissions.CHAT_PERMISSIONS.contains(permission)) {
            return Result.ok(true);
        }

        return Result.ok(false);
    }

    private Result<Boolean, Runnable> handleCommandPermissions(
            Guild guild,
            User user,
            GuildCommandPermission permission
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
        return Result.ok(false);
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
            boolean isSufficientPermission,
            User user,
            Function<MessageConfiguration, Sendable> messageSupplier
    ) {
        return Result.when(
                isSufficientPermission,
                () -> true,
                () -> () -> this.messageService.getMessage(messageSupplier)
                        .receiver(user)
                        .send()
        );
    }

}
