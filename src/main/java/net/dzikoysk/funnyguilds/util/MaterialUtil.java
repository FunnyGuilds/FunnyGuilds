package net.dzikoysk.funnyguilds.util;


import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import org.bukkit.Material;

public class MaterialUtil {

    public static String getMaterialName(Material material) {
        PluginConfig config = Settings.getConfig();
        if (config.translatedMaterialsEnable && config.translatedMaterials.containsKey(material)) {
            return StringUtils.colored(Settings.getConfig().translatedMaterials.get(material));
        } else {
            return StringUtils.replace(material.toString().toLowerCase(), "_", " ");
        }
    }

}
