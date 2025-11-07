package net.dzikoysk.funnyguilds.guild.permission;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.EnumSet;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import panda.std.Option;

public final class StaticGuildPermissionController implements GuildPermissionController {

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

    private static final Collection<GuildPermission<Boolean>> GUILD_CHAT_PERMISSIONS = ImmutableSet.of(
            GenericGuildPermissions.GUILD_CHAT_USE,
            GenericGuildPermissions.GUILD_CHAT_SEE,
            GenericGuildPermissions.ALLY_CHAT_USE,
            GenericGuildPermissions.ALLY_CHAT_SEE,
            GenericGuildPermissions.GLOBAL_CHAT_USE,
            GenericGuildPermissions.GLOBAL_CHAT_SEE
    );
    
    private final PluginConfiguration pluginConfiguration;

    public StaticGuildPermissionController(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Option<T> getPermissionValue(
            Guild guild,
            User user,
            GuildPermission<T> permission
    ) {
        if (permission instanceof GuildCommandPermission) {
            return (Option<T>) this.getGuildCommandPermissionValue(
                    guild,
                    user,
                    (GuildCommandPermission) permission
            );
        } else if (GenericGuildPermissions.USER_POSITION.equals(permission)) {
            return (Option<T>) this.getGuildUserPositionValue(user);
        } else if (GUILD_CHAT_PERMISSIONS.contains(permission)) {
            return (Option<T>) Option.of(true);
        }
        return Option.none();
    }

    private Option<Boolean> getGuildCommandPermissionValue(
            Guild guild,
            User user,
            GuildCommandPermission permission
    ) {
        boolean value = false;
        if (MEMBER_PERMISSIONS.contains(permission)) {
            value = guild.isMember(user);
        }
        else if (MANAGER_PERMISSIONS.contains(permission)) {
            value = guild.isDeputy(user) || guild.isOwner(user);
        }
        else if (OWNER_PERMISSIONS.contains(permission)) {
            value = guild.isOwner(user);
        }
        return Option.of(value);
    }

    private Option<String> getGuildUserPositionValue(User user) {
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
        return Option.of(value);
    }

    @Override
    public Option<Boolean> getProtectionPermissionValue(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission,
            Event event
    ) {
        if (!(permission instanceof GuildProtectionPermission)) {
            return Option.none();
        }
        return Option.of(guild.isMember(user));
    }
}
