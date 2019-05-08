package net.dzikoysk.funnyguilds.util.commons.bukkit;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;

public final class MaterialUtils {

    public static Material parseMaterial(String string, boolean allowNullReturn) {
        if (string == null) {
            FunnyGuilds.getInstance().getPluginLogger().parser("Unknown material: null");
            return allowNullReturn ? null : Material.AIR;
        }

        String materialName = string.toUpperCase().replaceAll(" ", "_");
        Material material = MaterialAliaser.getByAlias(materialName);

        if (material !=null) {
            return material;
        }

        material = Material.getMaterial(materialName);

        if (material == null) {
            FunnyGuilds.getInstance().getPluginLogger().parser("Unknown material: " + string);
            return allowNullReturn ? null : Material.AIR;
        }

        return material;
    }

    public static Pair<Material, Byte> parseMaterialData(String string, boolean allowNullReturn) {
        if (string == null) {
            FunnyGuilds.getInstance().getPluginLogger().parser("Unknown materialdata: null");
            return allowNullReturn ? null : Pair.of(Material.AIR, (byte) 0);
        }

        String[] data = string.split(":");
        Material material = parseMaterial(data[0], allowNullReturn);

        if (material == null) {
            FunnyGuilds.getInstance().getPluginLogger().parser("Unknown material in materialdata: " + string);
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

        if (config.translatedMaterialsEnable && config.translatedMaterials.containsKey(material)) {
            return ChatUtils.colored(FunnyGuilds.getInstance().getPluginConfiguration().translatedMaterials.get(material));
        }
        else {
            return StringUtils.replace(material.toString().toLowerCase(), "_", " ");
        }
    }

    private MaterialUtils() {}

}
