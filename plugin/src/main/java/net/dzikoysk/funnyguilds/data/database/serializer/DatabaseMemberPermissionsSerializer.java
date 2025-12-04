package net.dzikoysk.funnyguilds.data.database.serializer;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.permission.member.GuildMemberPermissionType;
import net.dzikoysk.funnyguilds.guild.permission.member.GuildMemberPermissions;
import net.dzikoysk.funnyguilds.guild.permission.member.GuildPermissionsManager;
import panda.std.Option;

/**
 * Serializer for member permissions to/from SQL database.
 */
public final class DatabaseMemberPermissionsSerializer {

    private DatabaseMemberPermissionsSerializer() {
    }

    /**
     * Load all member permissions from the database for all guilds.
     */
    public static void loadAllPermissions(SQLTable table, GuildManager guildManager) {
        SQLBasicUtils.getSelectAll(table).executeQuery(result -> {
            // Group permissions by guild UUID
            Map<UUID, Map<UUID, GuildMemberPermissions>> guildPermissions = new HashMap<>();
            
            while (result.next()) {
                try {
                    String guildUuidStr = result.getString("guild_uuid");
                    String memberUuidStr = result.getString("member_uuid");
                    String permTypeStr = result.getString("permission_type");
                    boolean value = result.getBoolean("permission_value");
                    String changedByStr = result.getString("changed_by");
                    long changedAtMillis = result.getLong("changed_at");
                    
                    UUID guildUuid = UUID.fromString(guildUuidStr);
                    UUID memberUuid = UUID.fromString(memberUuidStr);
                    GuildMemberPermissionType permType = GuildMemberPermissionType.fromKeyString(permTypeStr);
                    
                    if (permType == null) {
                        FunnyGuilds.getPluginLogger().warning("Unknown permission type in database: " + permTypeStr);
                        continue;
                    }
                    
                    UUID changedBy = changedByStr != null && !changedByStr.isEmpty() ? UUID.fromString(changedByStr) : null;
                    
                    // Get or create the permissions map for this guild
                    Map<UUID, GuildMemberPermissions> members = guildPermissions.computeIfAbsent(guildUuid, k -> new HashMap<>());
                    GuildMemberPermissions memberPerms = members.computeIfAbsent(memberUuid, GuildMemberPermissions::new);
                    
                    memberPerms.setOverride(permType, value, changedBy);
                } catch (Exception e) {
                    FunnyGuilds.getPluginLogger().error("Failed to deserialize member permission", e);
                }
            }
            
            // Apply loaded permissions to guilds
            for (Map.Entry<UUID, Map<UUID, GuildMemberPermissions>> entry : guildPermissions.entrySet()) {
                UUID guildUuid = entry.getKey();
                Option<Guild> guildOption = guildManager.findByUUID(guildUuid);
                
                if (guildOption.isEmpty()) {
                    continue;
                }
                
                Guild guild = guildOption.get();
                GuildPermissionsManager permissionsManager = guild.getPermissionsManager();
                permissionsManager.setMemberPermissions(entry.getValue());
            }
        });
        
        FunnyGuilds.getPluginLogger().debug("Loaded member permissions from database");
    }
    
    /**
     * Save member permissions for a guild to the database.
     */
    public static void savePermissions(SQLTable table, Guild guild) {
        GuildPermissionsManager permissionsManager = guild.getPermissionsManager();
        Map<UUID, GuildMemberPermissions> allPermissions = permissionsManager.getAllMemberPermissions();
        
        // First, delete all existing permissions for this guild
        deleteGuildPermissions(table, guild.getUUID());
        
        if (allPermissions.isEmpty()) {
            return;
        }
        
        // Insert all permission overrides
        for (Map.Entry<UUID, GuildMemberPermissions> memberEntry : allPermissions.entrySet()) {
            UUID memberUuid = memberEntry.getKey();
            GuildMemberPermissions memberPerms = memberEntry.getValue();
            
            if (!memberPerms.hasAnyOverrides()) {
                continue;
            }
            
            for (Map.Entry<GuildMemberPermissionType, GuildMemberPermissions.PermissionOverride> overrideEntry : memberPerms.getAllOverrides().entrySet()) {
                GuildMemberPermissionType permType = overrideEntry.getKey();
                GuildMemberPermissions.PermissionOverride override = overrideEntry.getValue();
                
                insertPermission(
                        table,
                        guild.getUUID(),
                        memberUuid,
                        permType.getKeyString(),
                        override.getValue(),
                        override.getChangedBy(),
                        override.getChangedAt()
                );
            }
        }
    }
    
    /**
     * Delete all permissions for a guild.
     */
    public static void deleteGuildPermissions(SQLTable table, UUID guildUuid) {
        try {
            SQLNamedStatement statement = SQLBasicUtils.getDelete(table);
            statement.set("guild_uuid", guildUuid.toString());
            statement.executeUpdate();
        } catch (Exception e) {
            FunnyGuilds.getPluginLogger().error("Failed to delete guild permissions", e);
        }
    }
    
    private static void insertPermission(
            SQLTable table,
            UUID guildUuid,
            UUID memberUuid,
            String permissionType,
            boolean value,
            UUID changedBy,
            Instant changedAt
    ) {
        try {
            SQLNamedStatement statement = SQLBasicUtils.getInsert(table);
            statement.set("guild_uuid", guildUuid.toString());
            statement.set("member_uuid", memberUuid.toString());
            statement.set("permission_type", permissionType);
            statement.set("permission_value", value);
            statement.set("changed_by", changedBy != null ? changedBy.toString() : null);
            statement.set("changed_at", changedAt != null ? changedAt.toEpochMilli() : null);
            statement.executeUpdate();
        } catch (Exception e) {
            FunnyGuilds.getPluginLogger().error("Failed to insert member permission", e);
        }
    }
}
