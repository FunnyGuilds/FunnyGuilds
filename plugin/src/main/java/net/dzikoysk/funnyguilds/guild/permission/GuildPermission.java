package net.dzikoysk.funnyguilds.guild.permission;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;

/**
 * Represents a permission that can be assigned to a guild member.
 * @param <T> the type of the permission value
 */
public interface GuildPermission<T> {
    
    String PLUGIN_NAMESPACE = "funnyguilds"; // TODO: Retrieve from plugin instance
    
    GuildPermission<Boolean> BLOCK_BREAK_PROTECTION = booleanPermission("block-break-protection");
    GuildPermission<Boolean> BLOCK_PLACE_PROTECTION = booleanPermission("block-place-protection");
    GuildPermission<Boolean> BLOCK_IGNITE_PROTECTION = booleanPermission("block-ignite-protection");
    GuildPermission<Boolean> BUCKET_FILL_PROTECTION = booleanPermission("bucket-fill-protection");
    GuildPermission<Boolean> BUCKET_EMPTY_PROTECTION = booleanPermission("bucket-empty-protection");
    GuildPermission<Boolean> HANGING_PLACE_PROTECTION = booleanPermission("hanging-place-protection");
    GuildPermission<Boolean> HANGING_BREAK_PROTECTION = booleanPermission("hanging-break-protection");
    GuildPermission<Boolean> ENTITY_PLACE_PROTECTION = booleanPermission("entity-place-protection");
    
    /**
     * @return the unique key of the permission
     */
    Key key();
    
    /**
     * @return the type of the permission value
     */
    Class<T> getValueType();

    /**
     * @return a new guild permission with the given key and value type
     */
    static <T> GuildPermission<T> permission(@KeyPattern String key, Class<T> valueType) {
        return new SimpleGuildPermission<>(Key.key(PLUGIN_NAMESPACE, key), valueType);
    }
    
    static GuildPermission<Boolean> booleanPermission(@KeyPattern String key) {
        return permission(key, Boolean.class);
    }
}
