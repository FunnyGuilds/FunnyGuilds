package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.ConfigUtils;

import java.io.File;

public class Settings {

    private static final File SETTINGS = new File(FunnyGuilds.getInstance().getDataFolder(), "config.yml");
    private static PluginConfig settings;

    public Settings() {
        settings = ConfigUtils.loadConfig(SETTINGS, PluginConfig.class);
        settings.reload();
    }

    public static PluginConfig getConfig() {

        if (settings == null) {
            new Settings();
        }

        return settings;
    }

}
