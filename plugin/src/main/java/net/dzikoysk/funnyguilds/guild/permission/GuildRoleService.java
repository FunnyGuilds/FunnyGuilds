package net.dzikoysk.funnyguilds.guild.permission;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import net.dzikoysk.funnyguilds.event.guild.GuildRoleChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionDecision.Source;
import net.dzikoysk.funnyguilds.guild.permission.GuildRolePolicy.Role;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class GuildRoleService {

    private static final List<GuildPermission<Boolean>> EDITABLE = Stream.<GuildPermission<Boolean>>concat(
            Arrays.stream(GuildCommandPermission.values()).filter(permission -> switch (permission) {
                case LEAVE, DEPUTY, DELETE -> false;
                default -> true;
            }),
            Arrays.stream(GuildProtectionPermission.values())).toList();

    public List<GuildPermission<Boolean>> permissions() {
        return EDITABLE;
    }

    public boolean canManage(Guild guild, User actor) {
        return guild.isMember(actor) && (guild.isOwner(actor) || guild.isDeputy(actor)
                && decision(guild, actor, GuildCommandPermission.MANAGE_PERMISSIONS).allowed());
    }

    public UUID selectedRole(Guild guild, User target) {
        if (guild.isOwner(target)) {
            return GuildRolePolicy.OWNER;
        }
        if (guild.isDeputy(target)) {
            return GuildRolePolicy.DEPUTY;
        }
        GuildRolePolicy policy = guild.getRolePolicy();
        UUID assigned = policy.assignments().get(target.getUUID());
        return assigned != null && isCustom(assigned) && policy.roles().containsKey(assigned) ? assigned : GuildRolePolicy.MEMBER;
    }

    public Role createRole(Guild guild, User actor, UUID id, String rawName) {
        this.requireOwner(guild, actor);
        String name = rawName == null ? "" : rawName.strip();
        when(!isCustom(id) || guild.getRolePolicy().roles().containsKey(id) || name.isEmpty() || name.length() > 24
                        || guild.getRolePolicy().roles().values().stream().anyMatch(role -> role.name().equalsIgnoreCase(name)),
                config -> config.guildRoleInvalidName);
        Role role = new Role(name, Map.of());
        update(guild, actor, guild.getRolePolicy().withRole(id, role));
        return role;
    }

    public void deleteRole(Guild guild, User actor, UUID id) {
        this.requireOwner(guild, actor);
        when(!isCustom(id) || !guild.getRolePolicy().roles().containsKey(id), config -> config.guildRoleDenied);
        update(guild, actor, guild.getRolePolicy().removeRole(id));
    }

    public void setRolePermission(Guild guild, User actor, UUID role, GuildPermission<Boolean> permission, Boolean value) {
        boolean owner = guild.isOwner(actor);
        when(!this.canManage(guild, actor) || !EDITABLE.contains(permission) || role.equals(GuildRolePolicy.OWNER)
                || (!owner && (!role.equals(GuildRolePolicy.MEMBER) || !canDelegate(guild, actor, permission))), config -> config.guildRoleDenied);
        GuildRolePolicy policy = guild.getRolePolicy();
        Role stored = policy.roles().get(role);
        when(stored == null && isCustom(role), config -> config.guildRoleDenied);
        Role old = stored == null ? new Role("", Map.of()) : stored;
        update(guild, actor, policy.withRole(role, old.withPermission(permission.key().asString(), value)));
    }

    public void setOverride(Guild guild, User actor, User target, GuildPermission<Boolean> permission, Boolean value) {
        this.requireEditableTarget(guild, actor, target);
        when(!EDITABLE.contains(permission) || (!guild.isOwner(actor) && !canDelegate(guild, actor, permission)),
                config -> config.guildRoleDenied);
        update(guild, actor, guild.getRolePolicy().override(target.getUUID(), permission.key().asString(), value));
    }

    public void assign(Guild guild, User actor, User target, UUID role) {
        this.requireEditableTarget(guild, actor, target);
        GuildRolePolicy current = guild.getRolePolicy();
        when(guild.isDeputy(target) || (!role.equals(GuildRolePolicy.MEMBER)
                && (!isCustom(role) || !current.roles().containsKey(role))), config -> config.guildRoleDenied);
        GuildRolePolicy updated = current.assign(target.getUUID(), role.equals(GuildRolePolicy.MEMBER) ? null : role);
        if (!guild.isOwner(actor)) {
            UUID oldRole = this.selectedRole(guild, target);
            for (GuildPermission<Boolean> permission : EDITABLE) {
                boolean changed = decision(guild, target, permission, current).allowed() != decision(guild, target, permission, updated).allowed();
                when(changed && !canDelegate(guild, actor, permission), config -> config.guildRoleDenied);
                // Don't introduce inherited grants hidden by a player's current overrides.
                boolean inheritedChanged = roleDecision(current, oldRole, permission).allowed() != roleDecision(updated, role, permission).allowed();
                when(inheritedChanged && !canDelegate(guild, actor, permission),
                        config -> config.guildRoleDenied);
            }
        }
        update(guild, actor, updated);
    }

    public void setDelegation(Guild guild, User actor, GuildPermission<Boolean> permission, boolean allowed) {
        this.requireOwner(guild, actor);
        when(!EDITABLE.contains(permission) || permission == GuildCommandPermission.MANAGE_PERMISSIONS, config -> config.guildRoleDenied);
        update(guild, actor, guild.getRolePolicy().delegate(permission.key().asString(), allowed));
    }

    private static void update(Guild guild, User actor, GuildRolePolicy updated) {
        GuildRolePolicy previous = guild.getRolePolicy();
        if (!updated.equals(previous)) {
            guild.setRolePolicy(updated);
            Bukkit.getPluginManager().callEvent(new GuildRoleChangeEvent(guild, actor, previous, updated));
        }
    }

    private void requireEditableTarget(Guild guild, User actor, User target) {
        when(!this.canManage(guild, actor) || !guild.isMember(target) || guild.isOwner(target)
                || (!guild.isOwner(actor) && (actor.equals(target) || guild.isDeputy(target))), config -> config.guildRoleDenied);
    }

    private void requireOwner(Guild guild, User actor) {
        when(!guild.isMember(actor) || !guild.isOwner(actor), config -> config.guildRoleDenied);
    }

    private static boolean canDelegate(Guild guild, User actor, GuildPermission<Boolean> permission) {
        return permission != GuildCommandPermission.MANAGE_PERMISSIONS
                && guild.getRolePolicy().delegatedPermissions().contains(permission.key().asString())
                && decision(guild, actor, permission).allowed();
    }

    static boolean editable(GuildPermission<?> permission) {
        return EDITABLE.contains(permission);
    }

    static GuildPermissionDecision decision(Guild guild, User user, GuildPermission<Boolean> permission) {
        return decision(guild, user, permission, guild.getRolePolicy());
    }

    private static GuildPermissionDecision decision(Guild guild, User user, GuildPermission<Boolean> permission, GuildRolePolicy policy) {
        if (!guild.isMember(user)) {
            return new GuildPermissionDecision(false, Source.DEFAULT);
        }
        if (guild.isOwner(user)) {
            return new GuildPermissionDecision(true, Source.OWNER);
        }
        String key = permission.key().asString();
        Boolean override = policy.overrides().getOrDefault(user.getUUID(), Map.of()).get(key);
        if (override != null) {
            return new GuildPermissionDecision(override, Source.PLAYER);
        }
        UUID custom = guild.isDeputy(user) ? null : policy.assignments().get(user.getUUID());
        if (custom != null && isCustom(custom)) {
            Boolean value = configured(policy, custom, key);
            if (value != null) {
                return new GuildPermissionDecision(value, Source.CUSTOM_ROLE);
            }
        }
        Boolean value = configured(policy, guild.isDeputy(user) ? GuildRolePolicy.DEPUTY : GuildRolePolicy.MEMBER, key);
        return value != null ? new GuildPermissionDecision(value, Source.BUILT_IN_ROLE)
                : new GuildPermissionDecision(defaultAllowed(guild.isDeputy(user), permission), Source.DEFAULT);
    }

    public static GuildPermissionDecision roleDecision(GuildRolePolicy policy, UUID role, GuildPermission<Boolean> permission) {
        if (role.equals(GuildRolePolicy.OWNER)) {
            return new GuildPermissionDecision(true, Source.OWNER);
        }
        String key = permission.key().asString();
        Boolean value = configured(policy, role, key);
        if (value != null) {
            return new GuildPermissionDecision(value, isCustom(role) ? Source.CUSTOM_ROLE : Source.BUILT_IN_ROLE);
        }
        if (isCustom(role)) {
            return roleDecision(policy, GuildRolePolicy.MEMBER, permission);
        }
        return new GuildPermissionDecision(defaultAllowed(role.equals(GuildRolePolicy.DEPUTY), permission), Source.DEFAULT);
    }

    private static Boolean configured(GuildRolePolicy policy, UUID role, String key) {
        Role stored = policy.roles().get(role);
        return stored == null ? null : stored.permissions().get(key);
    }

    public static boolean isCustom(UUID role) {
        return GuildRolePolicy.isCustom(role);
    }

    private static boolean defaultAllowed(boolean deputy, GuildPermission<Boolean> permission) {
        if (permission instanceof GuildProtectionPermission) {
            return true;
        }
        return permission instanceof GuildCommandPermission command && switch (command) {
            case BASE, LEAVE -> true;
            case SET_BASE, EXTEND_VALIDITY, ENLARGE, INVITE, KICK, PVP, BUY_LIFE -> deputy;
            default -> false;
        };
    }
}
