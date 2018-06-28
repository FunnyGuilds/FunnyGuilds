package net.dzikoysk.funnyguilds.util.commons.bukkit;

import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;

public final class MaterialUtil {

    public static String getMaterialName(Material material) {
        PluginConfig config = Settings.getConfig();
        
        if (config.translatedMaterialsEnable && config.translatedMaterials.containsKey(material)) {
            return ChatUtils.colored(Settings.getConfig().translatedMaterials.get(material));
        } else {
            return StringUtils.replace(material.toString().toLowerCase(), "_", " ");
        }
    }

    private MaterialUtil() {}
}
