package net.dzikoysk.funnyguilds.guild.permission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record GuildRolePolicy(Map<UUID, Role> roles, Map<UUID, UUID> assignments,
                              Map<UUID, Map<String, Boolean>> overrides, Set<String> delegatedPermissions) {

    public static final UUID OWNER = new UUID(0, 1);
    public static final UUID DEPUTY = new UUID(0, 2);
    public static final UUID MEMBER = new UUID(0, 3);
    public static final GuildRolePolicy EMPTY = new GuildRolePolicy(Map.of(), Map.of(), Map.of(), Set.of());

    public static boolean isCustom(UUID role) {
        return !role.equals(OWNER) && !role.equals(DEPUTY) && !role.equals(MEMBER);
    }

    public GuildRolePolicy {
        roles = Map.copyOf(roles);
        assignments = Map.copyOf(assignments);
        Map<UUID, Map<String, Boolean>> immutable = new HashMap<>();
        overrides.forEach((member, values) -> immutable.put(member, Map.copyOf(values)));
        overrides = Map.copyOf(immutable);
        delegatedPermissions = Set.copyOf(delegatedPermissions);
    }

    public GuildRolePolicy withRole(UUID id, Role role) {
        return new GuildRolePolicy(with(this.roles, id, role), this.assignments, this.overrides, this.delegatedPermissions);
    }

    public GuildRolePolicy removeRole(UUID id) {
        Map<UUID, UUID> assigned = new HashMap<>(this.assignments);
        assigned.values().removeIf(id::equals);
        return new GuildRolePolicy(with(this.roles, id, null), assigned, this.overrides, this.delegatedPermissions);
    }

    public GuildRolePolicy assign(UUID member, UUID role) {
        return new GuildRolePolicy(this.roles, with(this.assignments, member, role), this.overrides, this.delegatedPermissions);
    }

    public GuildRolePolicy override(UUID member, String permission, Boolean value) {
        Map<String, Boolean> rules = with(this.overrides.getOrDefault(member, Map.of()), permission, value);
        return new GuildRolePolicy(this.roles, this.assignments, with(this.overrides, member, rules.isEmpty() ? null : rules), this.delegatedPermissions);
    }

    public GuildRolePolicy delegate(String permission, boolean allowed) {
        Set<String> updated = new HashSet<>(this.delegatedPermissions);
        if (allowed) {
            updated.add(permission);
        } else {
            updated.remove(permission);
        }
        return new GuildRolePolicy(this.roles, this.assignments, this.overrides, updated);
    }

    public GuildRolePolicy removeMember(UUID member) {
        if (!this.assignments.containsKey(member) && !this.overrides.containsKey(member)) {
            return this;
        }
        return new GuildRolePolicy(this.roles, with(this.assignments, member, null), with(this.overrides, member, null), this.delegatedPermissions);
    }

    private static <K, V> Map<K, V> with(Map<K, V> original, K key, V value) {
        Map<K, V> updated = new HashMap<>(original);
        if (value == null) {
            updated.remove(key);
        } else {
            updated.put(key, value);
        }
        return updated;
    }

    public record Role(String name, Map<String, Boolean> permissions) {

        public Role {
            permissions = Map.copyOf(permissions);
        }

        public Role withPermission(String permission, Boolean value) {
            return new Role(this.name, with(this.permissions, permission, value));
        }
    }
}
