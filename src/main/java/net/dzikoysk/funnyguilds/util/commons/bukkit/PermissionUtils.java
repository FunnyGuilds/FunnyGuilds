package net.dzikoysk.funnyguilds.util.commons.bukkit;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.hook.VaultHook;
import org.bukkit.OfflinePlayer;

public final class PermissionUtils {
    private PermissionUtils() {}

    public static boolean isPrivileged(User user, String permission) {
        OfflinePlayer offlinePlayer = user.getOfflinePlayer();

        return offlinePlayer.isOp() ||
                VaultHook.isPermissionHooked() && VaultHook.hasPermission(offlinePlayer, permission);
    }
}
