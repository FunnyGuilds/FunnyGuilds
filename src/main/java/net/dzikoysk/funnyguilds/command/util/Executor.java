package net.dzikoysk.funnyguilds.command.util;

import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.util.commons.spigot.ItemComponentUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Executor {

    void execute(CommandSender sender, String[] args);

    default boolean checkWorld(Player player) {
        List<String> blockedWorlds = Settings.getConfig().blockedWorlds;
        return blockedWorlds != null && blockedWorlds.size() > 0 && blockedWorlds.contains(player.getWorld().getName());
    }

    default boolean playerHasEnoughItems(Player player, List<ItemStack> requiredItems) {
        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();

        for (ItemStack requiredItem : requiredItems) {
            if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                continue;
            }

            if (config.enableItemComponent) {
                player.spigot().sendMessage(ItemComponentUtils.translateComponentPlaceholder(messages.createItems, requiredItems, requiredItem));
            } else {
                player.sendMessage(ItemUtils.translateTextPlaceholder(messages.createItems, requiredItems, requiredItem));
            }

            return false;
        }

        return true;
    }
}