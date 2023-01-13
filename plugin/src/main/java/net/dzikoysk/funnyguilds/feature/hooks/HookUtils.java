package net.dzikoysk.funnyguilds.feature.hooks;

import org.bukkit.entity.Player;

public final class HookUtils {

    private HookUtils() {
    }

    public static String replacePlaceholders(Player observer, Player source, String message) {
        return HookManager.PLACEHOLDER_API.map(api -> api.replacePlaceholders(observer, source, message)).orElseGet(message);
    }

    public static String replacePlaceholders(Player player, String message) {
        return HookManager.PLACEHOLDER_API.map(api -> api.replacePlaceholders(player, message)).orElseGet(message);
    }

}
