package net.dzikoysk.funnyguilds.feature.hooks;

import net.dzikoysk.funnyguilds.feature.hooks.mvdwplaceholderapi.MVdWPlaceholderAPIHook;
import net.dzikoysk.funnyguilds.feature.hooks.placeholderapi.PlaceholderAPIHook;
import org.bukkit.entity.Player;

public final class HookUtils {

    private HookUtils() {
    }

    public static String replacePlaceholders(Player playerOne, Player playerTwo, String message) {
        message = replacePlaceholdersWithPlaceholderAPI(playerOne, playerTwo, message);
        message = replacePlaceholdersWithMVdWPlaceholderAPI(playerOne, message);
        return message;
    }

    public static String replacePlaceholders(Player player, String message) {
        return replacePlaceholders(player, null, message);
    }

    private static String replacePlaceholdersWithPlaceholderAPI(Player playerOne, Player playerTwo, String message) {
        return HookManager.PLACEHOLDER_API
                .map(api -> {
                    String replaceMessage = PlaceholderAPIHook.replacePlaceholders(playerOne, message);
                    if (playerTwo != null) {
                        replaceMessage = PlaceholderAPIHook.replacePlaceholders(playerOne, playerTwo, replaceMessage);
                    }

                    return replaceMessage;
                }).orElseGet(message);
    }

    private static String replacePlaceholdersWithMVdWPlaceholderAPI(Player player, String message) {
        return HookManager.MVDW_PLACEHOLDER_API
                .map(api -> MVdWPlaceholderAPIHook.replacePlaceholders(player, message))
                .orElseGet(message);
    }

}
