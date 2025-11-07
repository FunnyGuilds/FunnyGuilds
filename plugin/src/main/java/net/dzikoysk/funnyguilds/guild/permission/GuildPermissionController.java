package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public interface GuildPermissionController {

    /**
     * Get the value of a specific permission for a user
     * @param guild the guild to check
     * @param user the user to check
     * @param permission the permission to check
     * @return the value of the permission, or an empty option if not set
     * @param <T> the type of the permission value
     */
    <T> Option<T> getPermissionValue(Guild guild, User user, GuildPermission<T> permission);
    
    default <T> Option<T> getPermissionValue(User user, GuildPermission<T> permission) {
        return user.getGuild()
                .flatMap(guild -> this.getPermissionValue(guild, user, permission));
    }
    
    /**
     * Check if a user can perform an action based on a boolean permission
     * @param guild the guild to check
     * @param user the user to check
     * @param permission the permission to check
     * @return true if the user can perform the action, false otherwise
     */
    default boolean canPerformAction(Guild guild, User user, GuildPermission<Boolean> permission) {
        return this.getPermissionValue(guild, user, permission)
                .filter(value -> value)
                .isPresent();
    }
    
    default boolean canPerformAction(User user, GuildPermission<Boolean> permission) {
        return this.getPermissionValue(user, permission)
                .filter(value -> value)
                .isPresent();
    }
}
