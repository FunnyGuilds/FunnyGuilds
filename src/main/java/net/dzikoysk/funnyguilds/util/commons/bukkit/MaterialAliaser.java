package net.dzikoysk.funnyguilds.util.commons.bukkit;

import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public final class MaterialAliaser {

    private static final Map<String, Material> aliases = new HashMap<>();
    
    static {
        aliases.put("SKULL", Reflections.USE_PRE_13_METHODS ? Material.valueOf("SKULL_ITEM") : Material.valueOf("PLAYER_HEAD"));
        aliases.put("HEAD", Reflections.USE_PRE_13_METHODS ? Material.valueOf("SKULL_ITEM") : Material.valueOf("PLAYER_HEAD"));
        aliases.put("SPAWN_EGG", Reflections.USE_PRE_13_METHODS ? Material.valueOf("MONSTER_EGG") : Material.valueOf("PIG_SPAWN_EGG"));
        aliases.put("NETHER_WART", Reflections.USE_PRE_13_METHODS ? Material.valueOf("NETHER_STALK") : Material.valueOf("NETHER_WART"));
        aliases.put("ENCHANT", Reflections.USE_PRE_13_METHODS ? Material.valueOf("ENCHANTMENT_TABLE") : Material.valueOf("ENCHANTING_TABLE"));
        aliases.put("ENCHANTMENT_TABLE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("ENCHANTMENT_TABLE") : Material.valueOf("ENCHANTING_TABLE"));
        aliases.put("END_STONE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("ENDER_STONE") : Material.valueOf("END_STONE"));
        aliases.put("GOLD_APPLE", Material.GOLDEN_APPLE);
        aliases.put("GOLD_CARROT", Material.GOLDEN_CARROT);
        aliases.put("SPAWNER", Reflections.USE_PRE_13_METHODS ? Material.valueOf("MOB_SPAWNER") : Material.valueOf("SPAWNER"));
        aliases.put("MYCELIUM", Reflections.USE_PRE_13_METHODS ? Material.valueOf("MYCEL") : Material.valueOf("MYCELIUM"));
        aliases.put("PISTON", Reflections.USE_PRE_13_METHODS ? Material.valueOf("PISTON_BASE") : Material.valueOf("PISTON"));
        aliases.put("STICKY_PISTON", Reflections.USE_PRE_13_METHODS ? Material.valueOf("PISTON_STICKY_BASE") : Material.valueOf("STICKY_PISTON"));
        
        // Dyes
        aliases.put("DYE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("INK_SAC"));
        aliases.put("INK", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("INK_SAC"));
        aliases.put("INK_SAC", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("INK_SAC"));
        aliases.put("ROSE_RED", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("ROSE_RED"));
        aliases.put("CACTUS_GREEN", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("CACTUS_GREEN"));
        aliases.put("COCOA_BEANS", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("COCOA_BEANS"));
        aliases.put("LAPIS_LAZULI", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("LAPIS_LAZULI"));
        aliases.put("LAPIS", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("LAPIS_LAZULI"));
        aliases.put("PURPLE_DYE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("PURPLE_DYE"));
        aliases.put("CYAN_DYE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("CYAN_DYE"));
        aliases.put("LIGHT_GRAY_DYE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("LIGHT_GRAY_DYE"));
        aliases.put("GRAY_DYE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("GRAY_DYE"));
        aliases.put("PINK_DYE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("PINK_DYE"));
        aliases.put("LIME_DYE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("LIME_DYE"));
        aliases.put("DANDELION_YELLOW", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("DANDELION_YELLOW"));
        aliases.put("LIGHT_BLUE_DYE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("LIGHT_BLUE_DYE"));
        aliases.put("MAGENTA_DYE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("MAGENTA_DYE"));
        aliases.put("ORANGE_DYE", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("ORANGE_DYE"));
        aliases.put("BONE_MEAL", Reflections.USE_PRE_13_METHODS ? Material.valueOf("INK_SACK") : Material.valueOf("BONE_MEAL"));
        
        // For internal use
        aliases.put("GUI_ERROR", Reflections.USE_PRE_13_METHODS ? Material.valueOf("STAINED_GLASS_PANE") : Material.valueOf("RED_STAINED_GLASS_PANE"));
    }
    
    public static Material getByAlias(String alias) {
        return aliases.get(alias);
    }
}