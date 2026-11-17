package net.dzikoysk.funnyguilds.guild.permission;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;

/**
 * Represents a permission that can be assigned to a guild member.
 * @param <T> the type of the permission value
 */
public interface GuildPermission<T> {
    
    String PLUGIN_NAMESPACE = "funnyguilds"; // TODO: Retrieve from plugin instance
    
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
