package net.dzikoysk.funnyguilds.feature.hooks;

import org.bukkit.entity.Player;

public final class HookUtils {

    private HookUtils() {}

    public static String replacePlaceholders(Player userOne, Player userTwo, String message) {
        message = replacePlaceholdersWithPlaceholderAPI(userOne, userTwo, message);
        message = replacePlaceholdersWithMVdWPlaceholderAPI(userOne, message);
        return message;
    }

    public static String replacePlaceholders(Player user, String message) {
        return replacePlaceholdersWithPlaceholderAPI(user, null, message);
    }

    private static String replacePlaceholdersWithPlaceholderAPI(Player userOne, Player userTwo, String message) {
        return HookManager.PLACEHOLDER_API
                .map(api -> {
                    String replaceMessage = api.replacePlaceholders(userOne, message);
                    if (userTwo != null) {
                        replaceMessage = api.replacePlaceholders(userOne, userTwo, replaceMessage);
                    }
                    return replaceMessage;
                })
                .orElseGet(message);
    }

    private static String replacePlaceholdersWithMVdWPlaceholderAPI(Player user, String message) {
        return HookManager.MVDW_PLACEHOLDER_API
                .map(api -> api.replacePlaceholders(user, message))
                .orElseGet(message);
    }

}
