package net.dzikoysk.funnyguilds.system.protection;

import net.dzikoysk.funnyguilds.util.hook.PluginHook;
import net.dzikoysk.funnyguilds.util.hook.WorldGuardHook;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

public final class ProtectionUtils {

    public static boolean action(Action action, Block block) {
        if (action == Action.RIGHT_CLICK_BLOCK) {
            return checkBlock(block);
        }
        return false;
    }

    private static boolean checkBlock(Block block) {
        if (PluginHook.isPresent(PluginHook.PLUGIN_WORLDGUARD)) {
            if (WorldGuardHook.isOnRegion(block.getLocation())) {
                return false;
            }
        }

        switch (block.getType()) {
            case CHEST:
            case ENCHANTMENT_TABLE:
            case FURNACE:
            case ENDER_CHEST:
            case WORKBENCH:
            case ANVIL:
                return true;
            default:
                return false;
        }
    }

    private ProtectionUtils() {

    }
}
