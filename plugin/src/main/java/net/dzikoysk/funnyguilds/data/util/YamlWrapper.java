package net.dzikoysk.funnyguilds.data.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class YamlWrapper extends YamlConfiguration {

    private final File file;

    public YamlWrapper(File file) {
        super();
        
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            super.load(file);
        } catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Failed to load the file!", exception);
        }

        this.file = file;
    }

    @Override
    public void save(File file) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            super.save(file);
        } catch (IOException ioException) {
            FunnyGuilds.getPluginLogger().error("Failed to save the file!", ioException);
        }
    }

    public void save() {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            super.save(this.file);
        } catch (IOException ioException) {
            FunnyGuilds.getPluginLogger().error("Failed to save the file!", ioException);
        }
    }

}
