package net.dzikoysk.funnyguilds.feature.hooks;

import org.bukkit.entity.Player;

public final class HookUtils {

    private HookUtils() { }

    public static String replacePlaceholders(Player userOne, Player userTwo, String message) {
        String finalMessage = message;
        message = HookManager.PLACEHOLDER_API
                .map(api -> {
                    String replaceMessage = api.replacePlaceholders(userOne, finalMessage);
                    if(userTwo != null) {
                        replaceMessage = api.replacePlaceholders(userOne, userTwo, replaceMessage);
                    }
                    return replaceMessage;
                })
                .orElseGet(message);

        String finalMessage2 = message;
        message = HookManager.MVDW_PLACEHOLDER_API
                .map(api -> api.replacePlaceholders(userOne, finalMessage2))
                .orElseGet(message);

        return message;
    }

    public static String replacePlaceholders(Player user, String message) {
        return replacePlaceholders(user, null, message);
    }

}
