package net.dzikoysk.funnyguilds.guild.permission.member;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

/**
 * Manages permissions for all members of a guild.
 * This class provides thread-safe access to member permission overrides.
 */
public class GuildPermissionsManager {
    
    private final Guild guild;
    private final Map<UUID, GuildMemberPermissions> memberPermissions;
    
    public GuildPermissionsManager(Guild guild) {
        this.guild = guild;
        this.memberPermissions = new ConcurrentHashMap<>();
    }
    
    /**
     * Get the role of a user in the guild.
     * @param user the user to check
     * @return the user's role, or MEMBER if user is not in the guild
     */
    public GuildRole getUserRole(User user) {
        if (this.guild.isOwner(user)) {
            return GuildRole.OWNER;
        }
        if (this.guild.isDeputy(user)) {
            return GuildRole.DEPUTY;
        }
        return GuildRole.MEMBER;
    }
    
    /**
     * Get or create permissions for a member.
     * @param memberUuid the member's UUID
     * @return the member's permissions
     */
    public GuildMemberPermissions getOrCreatePermissions(UUID memberUuid) {
        return this.memberPermissions.computeIfAbsent(memberUuid, GuildMemberPermissions::new);
    }
    
    /**
     * Get permissions for a member if they exist.
     * @param memberUuid the member's UUID
     * @return the member's permissions, or empty if none set
     */
    public Optional<GuildMemberPermissions> getPermissions(UUID memberUuid) {
        return Optional.ofNullable(this.memberPermissions.get(memberUuid));
    }
    
    /**
     * Get the effective permission value for a user.
     * @param user the user to check
     * @param permission the permission to check
     * @return true if the user has the permission
     */
    public boolean hasPermission(User user, GuildMemberPermissionType permission) {
        if (!this.guild.isMember(user)) {
            return false;
        }
        
        GuildRole role = this.getUserRole(user);
        
        // Owner always has all permissions
        if (role == GuildRole.OWNER) {
            return true;
        }
        
        GuildMemberPermissions perms = this.memberPermissions.get(user.getUUID());
        if (perms != null) {
            return perms.getEffectivePermission(permission, role);
        }
        
        // No overrides, use role default
        return role.hasDefaultPermission(permission);
    }
    
    /**
     * Set a permission override for a member.
     * @param memberUuid the member's UUID
     * @param permission the permission to set
     * @param value the value to set
     * @param changedBy UUID of the user who made the change
     */
    public void setPermission(UUID memberUuid, GuildMemberPermissionType permission, boolean value, UUID changedBy) {
        this.getOrCreatePermissions(memberUuid).setOverride(permission, value, changedBy);
        this.guild.markChanged();
    }
    
    /**
     * Remove a permission override for a member.
     * @param memberUuid the member's UUID
     * @param permission the permission to reset
     * @return true if an override was removed
     */
    public boolean resetPermission(UUID memberUuid, GuildMemberPermissionType permission) {
        GuildMemberPermissions perms = this.memberPermissions.get(memberUuid);
        if (perms != null && perms.removeOverride(permission)) {
            if (!perms.hasAnyOverrides()) {
                this.memberPermissions.remove(memberUuid);
            }
            this.guild.markChanged();
            return true;
        }
        return false;
    }
    
    /**
     * Reset all permission overrides for a member.
     * @param memberUuid the member's UUID
     */
    public void resetAllPermissions(UUID memberUuid) {
        if (this.memberPermissions.remove(memberUuid) != null) {
            this.guild.markChanged();
        }
    }
    
    /**
     * Copy permissions from one member to another.
     * @param sourceUuid the source member's UUID
     * @param targetUuid the target member's UUID
     * @param copiedBy UUID of the user who performed the copy
     */
    public void copyPermissions(UUID sourceUuid, UUID targetUuid, UUID copiedBy) {
        GuildMemberPermissions source = this.memberPermissions.get(sourceUuid);
        if (source != null && source.hasAnyOverrides()) {
            this.getOrCreatePermissions(targetUuid).copyOverridesFrom(source, copiedBy);
            this.guild.markChanged();
        }
    }
    
    /**
     * Apply permissions from one member to all members with a specific role.
     * @param sourceUuid the source member's UUID
     * @param targetRole the role to apply to
     * @param appliedBy UUID of the user who performed the apply
     */
    public void applyToRole(UUID sourceUuid, GuildRole targetRole, UUID appliedBy) {
        GuildMemberPermissions source = this.memberPermissions.get(sourceUuid);
        if (source == null || !source.hasAnyOverrides()) {
            return;
        }
        
        for (User member : this.guild.getMembers()) {
            if (this.getUserRole(member) == targetRole && !member.getUUID().equals(sourceUuid)) {
                this.getOrCreatePermissions(member.getUUID()).copyOverridesFrom(source, appliedBy);
            }
        }
        this.guild.markChanged();
    }
    
    /**
     * Remove permissions for a member who left the guild.
     * @param memberUuid the member's UUID
     * @param archive if true, permissions are archived instead of deleted
     */
    public void onMemberLeave(UUID memberUuid, boolean archive) {
        if (!archive) {
            this.memberPermissions.remove(memberUuid);
        }
        // If archive is true, we keep the permissions in case they rejoin
    }
    
    /**
     * @return an unmodifiable view of all member permissions
     */
    public Map<UUID, GuildMemberPermissions> getAllMemberPermissions() {
        return Collections.unmodifiableMap(this.memberPermissions);
    }
    
    /**
     * Set member permissions from a map (used during deserialization).
     * @param permissions the map of member permissions to set
     */
    public void setMemberPermissions(Map<UUID, GuildMemberPermissions> permissions) {
        this.memberPermissions.clear();
        if (permissions != null) {
            this.memberPermissions.putAll(permissions);
        }
    }
    
    /**
     * @return true if there are any permission overrides set
     */
    public boolean hasAnyOverrides() {
        return !this.memberPermissions.isEmpty();
    }
}
