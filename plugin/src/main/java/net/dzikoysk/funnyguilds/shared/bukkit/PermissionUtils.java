package net.dzikoysk.funnyguilds.shared.bukkit;

import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.OfflinePlayer;

public final class PermissionUtils {

    private PermissionUtils() {}

    public static boolean isPrivileged(User user, String permission) {
        OfflinePlayer offlinePlayer = user.getOfflinePlayer();

        return offlinePlayer.isOp() ||
                VaultHook.isPermissionHooked() && VaultHook.hasPermission(offlinePlayer, permission);
    }
}
