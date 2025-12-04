package net.dzikoysk.funnyguilds.guild.permission.member;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

/**
 * Stores permission overrides for a single guild member.
 * Permissions can be explicitly set (ON/OFF) or inherit from the member's role.
 */
public class GuildMemberPermissions {
    
    private final UUID memberUuid;
    private final Map<GuildMemberPermissionType, PermissionOverride> overrides;
    
    public GuildMemberPermissions(UUID memberUuid) {
        this.memberUuid = Objects.requireNonNull(memberUuid, "Member UUID cannot be null");
        this.overrides = new EnumMap<>(GuildMemberPermissionType.class);
    }
    
    /**
     * Create a copy of existing permissions.
     * @param other the permissions to copy
     */
    public GuildMemberPermissions(GuildMemberPermissions other) {
        this.memberUuid = other.memberUuid;
        this.overrides = new EnumMap<>(GuildMemberPermissionType.class);
        for (Map.Entry<GuildMemberPermissionType, PermissionOverride> entry : other.overrides.entrySet()) {
            this.overrides.put(entry.getKey(), new PermissionOverride(entry.getValue()));
        }
    }
    
    /**
     * @return the UUID of the guild member
     */
    public UUID getMemberUuid() {
        return this.memberUuid;
    }
    
    /**
     * Check if a permission has an explicit override.
     * @param permission the permission to check
     * @return true if there's an explicit override
     */
    public boolean hasOverride(GuildMemberPermissionType permission) {
        return this.overrides.containsKey(permission);
    }
    
    /**
     * Get the override value for a permission.
     * @param permission the permission to get
     * @return the override value, or empty if not set
     */
    public Optional<Boolean> getOverride(GuildMemberPermissionType permission) {
        PermissionOverride override = this.overrides.get(permission);
        return override != null ? Optional.of(override.getValue()) : Optional.empty();
    }
    
    /**
     * Get the effective permission value, considering the member's role.
     * @param permission the permission to check
     * @param role the member's role
     * @return the effective permission value
     */
    public boolean getEffectivePermission(GuildMemberPermissionType permission, GuildRole role) {
        return this.getOverride(permission).orElseGet(() -> role.hasDefaultPermission(permission));
    }
    
    /**
     * Get detailed override information.
     * @param permission the permission to get info for
     * @return the override details, or null if not set
     */
    @Nullable
    public PermissionOverride getOverrideDetails(GuildMemberPermissionType permission) {
        return this.overrides.get(permission);
    }
    
    /**
     * Set a permission override.
     * @param permission the permission to set
     * @param value the value to set (true = allow, false = deny)
     * @param changedBy UUID of the user who made the change
     */
    public void setOverride(GuildMemberPermissionType permission, boolean value, UUID changedBy) {
        this.overrides.put(permission, new PermissionOverride(value, changedBy, Instant.now()));
    }
    
    /**
     * Remove a permission override, returning to role default.
     * @param permission the permission to reset
     * @return true if an override was removed
     */
    public boolean removeOverride(GuildMemberPermissionType permission) {
        return this.overrides.remove(permission) != null;
    }
    
    /**
     * Reset all overrides to role defaults.
     */
    public void resetAllOverrides() {
        this.overrides.clear();
    }
    
    /**
     * @return an unmodifiable view of all overrides
     */
    public Map<GuildMemberPermissionType, PermissionOverride> getAllOverrides() {
        return Collections.unmodifiableMap(this.overrides);
    }
    
    /**
     * @return true if there are any overrides set
     */
    public boolean hasAnyOverrides() {
        return !this.overrides.isEmpty();
    }
    
    /**
     * Copy overrides from another member's permissions.
     * @param source the source permissions to copy from
     * @param copiedBy UUID of the user who performed the copy
     */
    public void copyOverridesFrom(GuildMemberPermissions source, UUID copiedBy) {
        Instant now = Instant.now();
        for (Map.Entry<GuildMemberPermissionType, PermissionOverride> entry : source.overrides.entrySet()) {
            this.overrides.put(entry.getKey(), new PermissionOverride(entry.getValue().getValue(), copiedBy, now));
        }
    }
    
    /**
     * Represents a single permission override with metadata.
     */
    public static class PermissionOverride {
        private final boolean value;
        private final UUID changedBy;
        private final Instant changedAt;
        
        public PermissionOverride(boolean value, UUID changedBy, Instant changedAt) {
            this.value = value;
            this.changedBy = changedBy;
            this.changedAt = changedAt;
        }
        
        public PermissionOverride(PermissionOverride other) {
            this.value = other.value;
            this.changedBy = other.changedBy;
            this.changedAt = other.changedAt;
        }
        
        public boolean getValue() {
            return this.value;
        }
        
        public UUID getChangedBy() {
            return this.changedBy;
        }
        
        public Instant getChangedAt() {
            return this.changedAt;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GuildMemberPermissions that = (GuildMemberPermissions) o;
        return Objects.equals(this.memberUuid, that.memberUuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.memberUuid);
    }
}
