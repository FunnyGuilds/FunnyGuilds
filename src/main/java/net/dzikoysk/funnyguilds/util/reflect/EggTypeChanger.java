package net.dzikoysk.funnyguilds.util.reflect;

import net.dzikoysk.funnyguilds.util.FunnyLogger;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EggTypeChanger {

    private static Class<?> spawnEggMetaClass;
    private static Method setSpawnedTypeMethod;
    
    static {
        try {
            spawnEggMetaClass = Class.forName("org.bukkit.inventory.meta.SpawnEggMeta");
        } catch (ClassNotFoundException ignored) {
            spawnEggMetaClass = null;
        }
        
        if (spawnEggMetaClass != null) {
            try {
                setSpawnedTypeMethod = spawnEggMetaClass.getMethod("setSpawnedType", EntityType.class);
            } catch (NoSuchMethodException | SecurityException ignored) {}
        }
    }
    
    public static boolean needsSpawnEggMeta() {
        return spawnEggMetaClass != null;
    }
    
    public static void applyChanges(ItemMeta meta, EntityType type) {
        Class<?>[] implementations = meta.getClass().getInterfaces();
        if (implementations.length == 0 || implementations[0] != spawnEggMetaClass) {
            return;
        }
        
        try {
            setSpawnedTypeMethod.invoke(meta, type);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            FunnyLogger.exception("Failed to set entity type for SpawnEggMeta object", e.getStackTrace());
        }
    }
    
}
