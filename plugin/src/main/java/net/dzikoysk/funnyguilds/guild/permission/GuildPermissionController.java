package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public interface GuildPermissionController {

    /**
     * Get the value of a specific permission for a user
     * @param user the user to check
     * @param permission the permission to check
     * @return the value of the permission, or an empty option if not set
     * @param <T> the type of the permission value
     */
    <T> Option<T> getPermissionValue(User user, GuildPermission<T> permission);

    /**
     * Check if a user can perform an action based on a permission and required value
     * @param user the user to check
     * @param permission the permission to check
     * @param requiredValue the required value to perform the action
     * @return true if the user can perform the action, false otherwise
     * @param <T> the type of the permission value
     */
    default <T> boolean canPerformAction(User user, GuildPermission<T> permission, T requiredValue) {
        return this.getPermissionValue(user, permission)
                .map(value -> value.equals(requiredValue))
                .isPresent();
    }
    
    /**
     * Check if a user can perform an action based on a boolean permission
     * @param user the user to check
     * @param permission the permission to check
     * @return true if the user can perform the action, false otherwise
     */
    default boolean canPerformAction(User user, GuildPermission<Boolean> permission) {
        return this.getPermissionValue(user, permission)
                .filter(value -> value)
                .isPresent();
    }
}
