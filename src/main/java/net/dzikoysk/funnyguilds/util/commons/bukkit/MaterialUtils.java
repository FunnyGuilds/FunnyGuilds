package net.dzikoysk.funnyguilds.util.commons.bukkit;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MaterialUtils {

    private static final Method MATCH_MATERIAL_METHOD =
            Reflections.getMethod(Material.class, "matchMaterial", String.class, boolean.class);

    public static Material parseMaterial(String string, boolean allowNullReturn) {
        if (string == null) {
            FunnyGuilds.getInstance().getPluginLogger().parser("Unknown material: null");
            return allowNullReturn ? null : Material.AIR;
        }

        String materialName = string.toUpperCase().replaceAll(" ", "_");
        Material material = matchMaterial(materialName);

        if (material == null) {
            FunnyGuilds.getInstance().getPluginLogger().parser("Unknown material: " + string);
            return allowNullReturn ? null : Material.AIR;
        }

        return material;
    }

    public static Pair<Material, Byte> parseMaterialData(String string, boolean allowNullReturn) {
        if (string == null) {
            FunnyGuilds.getInstance().getPluginLogger().parser("Unknown material data: null");
            return allowNullReturn ? null : Pair.of(Material.AIR, (byte) 0);
        }

        String[] data = string.split(":");
        Material material = parseMaterial(data[0], allowNullReturn);

        if (material == null) {
            FunnyGuilds.getInstance().getPluginLogger().parser("Unknown material in material data: " + string);
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


    private MaterialUtils() {}

}
