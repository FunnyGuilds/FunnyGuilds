package net.dzikoysk.funnyguilds.shared.bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.nms.Reflections;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public final class MaterialUtils {

    private static final Method MATCH_MATERIAL_METHOD =
            Reflections.getMethod(Material.class, "matchMaterial", String.class, boolean.class);

    private MaterialUtils() {}

    public static Material parseMaterial(String string, boolean allowNullReturn) {
        if (string == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown material: null");
            return allowNullReturn ? null : Material.AIR;
        }

        String materialName = string.toUpperCase().replaceAll(" ", "_");
        Material material = matchMaterial(materialName);

        if (material == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown material: " + string);
            return allowNullReturn ? null : Material.AIR;
        }

        return material;
    }

    public static Pair<Material, Byte> parseMaterialData(String string, boolean allowNullReturn) {
        if (string == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown material data: null");
            return allowNullReturn ? null : Pair.of(Material.AIR, (byte) 0);
        }

        String[] data = string.split(":");
        Material material = parseMaterial(data[0], allowNullReturn);

        if (material == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown material in material data: " + string);
            return allowNullReturn ? null : Pair.of(Material.AIR, (byte) 0);
        }


        return Pair.of(material, data.length == 2 ? Byte.parseByte(data[1]) : (byte) 0);
    }

    public static boolean hasGravity(Material material) {
        switch (material.toString()) {
            case "DRAGON_EGG":
            case "SAND":
            case "GRAVEL":
            case "ANVIL":
            case "CONCRETE_POWDER":
                return true;
            default:
                return false;
        }
    }


    public static String getMaterialName(Material material) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (!config.translatedMaterialsEnable) {
            return material.toString();
        }

        if (config.translatedMaterials.containsKey(material)) {
            return ChatUtils.colored(FunnyGuilds.getInstance().getPluginConfiguration().translatedMaterials.get(material));
        }

        return StringUtils.replaceChars(material.toString().toLowerCase(), '_', ' ');
    }

    public static String getItemCustomName(ItemStack itemStack) {
        if (itemStack == null) {
            return "";
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return "";
        }

        if (!itemMeta.hasDisplayName()) {
            return "";
        }

        return itemMeta.getDisplayName();
    }

    @Nullable
    public static Material matchMaterial(String materialName) {
        try {
            if (Reflections.USE_PRE_13_METHODS) {
                return Material.matchMaterial(materialName);
            }

            if (MATCH_MATERIAL_METHOD == null) {
                return null;
            }

            Material material = (Material) MATCH_MATERIAL_METHOD.invoke(null, materialName, false);

            if (material == null) {
                material = (Material) MATCH_MATERIAL_METHOD.invoke(null, materialName, true);
            }

            return material;
        }
        catch (IllegalAccessException | InvocationTargetException ex) {
            return null;
        }
    }

}
