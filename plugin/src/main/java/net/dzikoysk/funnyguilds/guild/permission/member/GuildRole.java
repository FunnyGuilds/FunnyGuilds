package net.dzikoysk.funnyguilds.guild.permission.member;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Enum representing different roles within a guild.
 * Each role has a set of default permissions that members with that role inherit.
 */
public enum GuildRole {
    
    /**
     * Guild owner - has all permissions by default.
     */
    OWNER("Lider", 3),
    
    /**
     * Guild deputy - has most permissions except critical ones.
     */
    DEPUTY("Zastępca", 2),
    
    /**
     * Regular guild member - has basic permissions.
     */
    MEMBER("Członek", 1);
    
    private final String displayName;
    private final int priority;
    private final Set<GuildMemberPermissionType> defaultPermissions;
    
    GuildRole(String displayName, int priority) {
        this.displayName = displayName;
        this.priority = priority;
        this.defaultPermissions = createDefaultPermissions();
    }
    
    private Set<GuildMemberPermissionType> createDefaultPermissions() {
        return switch (this) {
            case OWNER -> EnumSet.allOf(GuildMemberPermissionType.class);
            case DEPUTY -> EnumSet.of(
                    // Block operations
                    GuildMemberPermissionType.BLOCK_PLACE,
                    GuildMemberPermissionType.BLOCK_BREAK,
                    // Special blocks
                    GuildMemberPermissionType.OBSIDIAN_PLACE,
                    GuildMemberPermissionType.OBSIDIAN_BREAK,
                    GuildMemberPermissionType.TNT_PLACE,
                    GuildMemberPermissionType.TNT_IGNITE,
                    // Liquids
                    GuildMemberPermissionType.BUCKET_FILL,
                    GuildMemberPermissionType.BUCKET_EMPTY,
                    // Containers
                    GuildMemberPermissionType.CHEST_OPEN,
                    GuildMemberPermissionType.CHEST_MODIFY,
                    GuildMemberPermissionType.ENDER_CHEST_OPEN,
                    // Interactions
                    GuildMemberPermissionType.DOOR_USE,
                    GuildMemberPermissionType.BUTTON_USE,
                    GuildMemberPermissionType.LEVER_USE,
                    GuildMemberPermissionType.PRESSURE_PLATE_USE,
                    // Entity operations
                    GuildMemberPermissionType.ENTITY_PLACE,
                    GuildMemberPermissionType.ENTITY_DAMAGE,
                    GuildMemberPermissionType.ENTITY_INTERACT,
                    // Hanging items
                    GuildMemberPermissionType.HANGING_PLACE,
                    GuildMemberPermissionType.HANGING_BREAK,
                    // Redstone
                    GuildMemberPermissionType.REDSTONE_USE,
                    // Special guild actions
                    GuildMemberPermissionType.HOME_TELEPORT,
                    GuildMemberPermissionType.INVITE_MEMBERS,
                    GuildMemberPermissionType.KICK_MEMBERS
            );
            case MEMBER -> EnumSet.of(
                    // Basic block operations
                    GuildMemberPermissionType.BLOCK_PLACE,
                    GuildMemberPermissionType.BLOCK_BREAK,
                    // Liquids
                    GuildMemberPermissionType.BUCKET_FILL,
                    GuildMemberPermissionType.BUCKET_EMPTY,
                    // Containers (read-only by default)
                    GuildMemberPermissionType.CHEST_OPEN,
                    GuildMemberPermissionType.ENDER_CHEST_OPEN,
                    // Interactions
                    GuildMemberPermissionType.DOOR_USE,
                    GuildMemberPermissionType.BUTTON_USE,
                    GuildMemberPermissionType.LEVER_USE,
                    GuildMemberPermissionType.PRESSURE_PLATE_USE,
                    // Entity interactions
                    GuildMemberPermissionType.ENTITY_INTERACT,
                    // Redstone
                    GuildMemberPermissionType.REDSTONE_USE,
                    // Base teleport
                    GuildMemberPermissionType.HOME_TELEPORT
            );
        };
    }
    
    /**
     * @return the display name of this role
     */
    public String getDisplayName() {
        return this.displayName;
    }
    
    /**
     * @return the priority of this role (higher = more authority)
     */
    public int getPriority() {
        return this.priority;
    }
    
    /**
     * @return an unmodifiable set of default permissions for this role
     */
    public Set<GuildMemberPermissionType> getDefaultPermissions() {
        return Collections.unmodifiableSet(this.defaultPermissions);
    }
    
    /**
     * Check if this role has a specific permission by default.
     * @param permission the permission to check
     * @return true if this role has the permission by default
     */
    public boolean hasDefaultPermission(GuildMemberPermissionType permission) {
        return this.defaultPermissions.contains(permission);
    }
    
    /**
     * Create a map of all permissions with their default values for this role.
     * @return a map of permission types to their default boolean values
     */
    public Map<GuildMemberPermissionType, Boolean> createDefaultPermissionMap() {
        Map<GuildMemberPermissionType, Boolean> map = new EnumMap<>(GuildMemberPermissionType.class);
        for (GuildMemberPermissionType type : GuildMemberPermissionType.values()) {
            map.put(type, this.defaultPermissions.contains(type));
        }
        return map;
    }
}
