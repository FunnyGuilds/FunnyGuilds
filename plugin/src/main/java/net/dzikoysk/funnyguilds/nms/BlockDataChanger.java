package net.dzikoysk.funnyguilds.nms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.block.Block;

public final class BlockDataChanger {

    private static Method setDataMethod;

    static {
        Class<?> craftBlockClass = Reflections.getCraftBukkitClass("block.CraftBlock");
        setDataMethod = Reflections.getMethod(craftBlockClass, "setData", byte.class);
    }

    private BlockDataChanger() {
    }

    public static void applyChanges(Block targetBlock, byte newData) {
        if (!Reflections.USE_PRE_13_METHODS) {
            return;
        }

        try {
            setDataMethod.invoke(targetBlock, newData);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            FunnyGuilds.getPluginLogger().error("Failed to change block data for a block at: " + LocationUtils.toString(targetBlock.getLocation()), ex);
        }
    }

}
