package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import panda.std.Option;
import panda.std.Result;

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

    /**
     * Get the value of a specific protection permission for a user, or fallback action if user lacks permission
     * @param guild the guild to check
     * @param user the user to check
     * @param permission the permission to check
     * @return result containing the value of the permission or a runnable action to execute if permission is denied
     */
    Result<Boolean, Runnable> getPermissionResult(Guild guild, User user, GuildPermission<Boolean> permission);

    /**
     * Get the value of a specific protection permission for a user, or fallback action if user lacks permission
     * @param guild the guild to check
     * @param user the user to check
     * @param permission the permission to check
     * @param event the event related to the protection action
     * @return result containing the value of the permission or a runnable action to execute if permission is denied
     */
    Result<Boolean, Runnable> getProtectionPermissionResult(Guild guild, User user, GuildPermission<Boolean> permission, Event event);
    
    /**
     * Handle the permission check for a user, returning a boolean result and sending appropriate messages
     * @param guild the guild to check
     * @param user the user to check
     * @param permission the permission to check
     * @return true if the user has the permission, false otherwise
     */
    default boolean handlePermission(Guild guild, User user, GuildPermission<Boolean> permission) {
        return this.getPermissionResult(guild, user, permission)
                .mapErr(GuildPermissionController::handleError)
                .get();
    }
    
    /**
     * Handle the protection permission check for a user, returning a boolean result and sending appropriate messages
     * @param guild the guild to check
     * @param user the user to check
     * @param permission the permission to check
     * @param event the event related to the protection action
     * @return true if the user has the permission, false otherwise
     */
    default boolean handleProtectionPermission(Guild guild, User user, GuildPermission<Boolean> permission, Event event) {
        return this.getProtectionPermissionResult(guild, user, permission, event)
                .mapErr(GuildPermissionController::handleError)
                .get();
    }
    
    static boolean handleError(Runnable errorAction) {
        errorAction.run();
        return false;
    }
}
