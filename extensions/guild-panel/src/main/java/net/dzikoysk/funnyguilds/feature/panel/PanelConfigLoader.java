package net.dzikoysk.funnyguilds.feature.panel;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.io.File;
import java.util.Map;
import java.util.logging.Logger;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.feature.items.gui.IconMaterials;
import org.bukkit.Material;

final class PanelConfigLoader {

    private PanelConfigLoader() {
    }

    public static PanelConfiguration load(File file, Logger logger) {
        PanelConfiguration panel = ConfigManager.create(PanelConfiguration.class, config -> {
            config.configure(options -> {
                options.configurer(new YamlBukkitConfigurer(), new SerdesCommons());
                options.bindFile(file);
                options.logger(logger);
            });
            config.saveDefaults();
            config.load(true);
        });
        panel.upgradeLayout();
        panel.validateLayout();
        validatePanelItems(panel.screens.overview.items);
        validatePanelItems(panel.screens.relations.items);
        validatePanelItems(panel.screens.relation.items);
        validatePanelItems(panel.screens.confirm.items);
        validatePanelItems(panel.screens.renewal.items);
        validatePanelItems(panel.screens.expansion.items);
        validatePanelItems(panel.screens.purchaseConfirm.items);
        validatePanelItems(panel.screens.roles.items);
        validatePanelItems(panel.screens.audit.items);
        panel.screens.expansionMaterials.forEach(PanelConfigLoader::validatePanelMaterial);
        panel.screens.permissionMaterials.forEach(PanelConfigLoader::validatePanelMaterial);
        return panel;
    }

    private static void validatePanelItems(Map<String, GuildItemDefinition> items) {
        items.forEach((key, item) -> validatePanelMaterial(key, item.material));
    }

    private static void validatePanelMaterial(String key, String name) {
        Material material = IconMaterials.resolve(name);
        if (material == null || !material.isItem() || material.isAir()) {
            throw new IllegalArgumentException("Invalid material for panel item " + key + ": " + name);
        }
    }
}
