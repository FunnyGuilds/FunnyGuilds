package net.dzikoysk.funnyguilds.feature.hooks;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class HookUtils {

    private HookUtils() {
    }

    public static Component replacePlaceholders(Player observer, Player target, Component message) {
        //return HookManager.PLACEHOLDER_API.map(api -> api.replacePlaceholders(observer, target, message)).orElseGet(message);
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: reimplment
    }

    public static Component replacePlaceholders(Player player, Component message) {
        //return HookManager.PLACEHOLDER_API.map(api -> api.replacePlaceholders(player, message)).orElseGet(message);
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: reimplment
    }

}
