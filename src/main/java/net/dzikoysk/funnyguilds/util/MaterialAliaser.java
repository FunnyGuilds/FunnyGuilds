package net.dzikoysk.funnyguilds.util;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public final class MaterialAliaser {

    private static final Map<String, Material> aliases = new HashMap<>();
    
    static {
        aliases.put("SKULL", Material.SKULL_ITEM);
        aliases.put("HEAD", Material.SKULL_ITEM);
        aliases.put("SPAWN_EGG", Material.MONSTER_EGG);
        aliases.put("NETHER_WART", Material.NETHER_STALK);
        aliases.put("ENCHANT", Material.ENCHANTMENT_TABLE);
        aliases.put("END_STONE", Material.ENDER_STONE);
        aliases.put("GOLD_APPLE", Material.GOLDEN_APPLE);
        aliases.put("GOLD_CARROT", Material.GOLDEN_CARROT);
        aliases.put("SPAWNER", Material.MOB_SPAWNER);
        aliases.put("MYCELIUM", Material.MYCEL);
        aliases.put("PISTON", Material.PISTON_BASE);
        aliases.put("STICKY_PISTON", Material.PISTON_STICKY_BASE);
        
        // Dyes
        aliases.put("DYE", Material.INK_SACK);
        aliases.put("ROSE_RED", Material.INK_SACK);
        aliases.put("CACTUS_GREEN", Material.INK_SACK);
        aliases.put("COCOA_BEANS", Material.INK_SACK);
        aliases.put("LAPIS_LAZULI", Material.INK_SACK);
        aliases.put("LAPIS", Material.INK_SACK);
        aliases.put("PURPLE_DYE", Material.INK_SACK);
        aliases.put("CYAN_DYE", Material.INK_SACK);
        aliases.put("LIGHT_GRAY_DYE", Material.INK_SACK);
        aliases.put("GRAY_DYE", Material.INK_SACK);
        aliases.put("PINK_DYE", Material.INK_SACK);
        aliases.put("LIME_DYE", Material.INK_SACK);
        aliases.put("DANDELION_YELLOW", Material.INK_SACK);
        aliases.put("LIGHT_BLUE_DYE", Material.INK_SACK);
        aliases.put("MAGENTA_DYE", Material.INK_SACK);
        aliases.put("ORANGE_DYE", Material.INK_SACK);
        aliases.put("BONE_MEAL", Material.INK_SACK);
    }
    
    public static Material getByAlias(String alias) {
        return aliases.get(alias);
    }
}