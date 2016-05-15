package net.dzikoysk.funnyguilds.system.protection;

import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

public class ProtectionUtils {

    public static boolean action(Action action, Block block) {
        return action == Action.RIGHT_CLICK_BLOCK && checkBlock(block);
    }

    private static boolean checkBlock(Block block) {
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

}
