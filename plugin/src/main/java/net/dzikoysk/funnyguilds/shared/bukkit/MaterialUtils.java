package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class MaterialUtils {

    private MaterialUtils() { }

    public static String getMaterialName(Material material) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (!config.translatedMaterialsEnable) {
            return material.toString();
        }

        if (config.translatedMaterials.containsKey(material)) {
            return ChatUtils.colored(config.translatedMaterials.get(material));
        }

        return FunnyFormatter.format(material.toString().toLowerCase(Locale.ROOT), "_", " ");
    }

    public static String getItemCustomName(ItemStack itemStack) {
        if (itemStack == null) {
            return "";
        }
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return "";
        }

        Component displayName = itemMeta.displayName();

        if (displayName == null) {
            return "";
        }
        return PlainTextComponentSerializer.plainText().serialize(displayName);
    }

}
