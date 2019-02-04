package net.dzikoysk.funnyguilds.command.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.util.commons.spigot.ItemComponentUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public interface Executor {

    void execute(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    default boolean checkWorld(Player player) {
        List<String> blockedWorlds = FunnyGuilds.getInstance().getPluginConfiguration().blockedWorlds;
        return blockedWorlds != null && blockedWorlds.size() > 0 && blockedWorlds.contains(player.getWorld().getName());
    }

    default boolean playerHasEnoughItems(Player player, List<ItemStack> requiredItems) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

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