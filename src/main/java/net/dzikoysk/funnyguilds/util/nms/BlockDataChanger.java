package net.dzikoysk.funnyguilds.util.nms;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.block.Block;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlockDataChanger {

    private static Class<?> craftBlockClass;
    private static Method setDataMethod;
    
    static {
        craftBlockClass = Reflections.getCraftBukkitClass("block.CraftBlock");
        setDataMethod = Reflections.getMethod(craftBlockClass, "setData", byte.class);
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

    private BlockDataChanger() {}
    
}
