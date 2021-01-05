package net.dzikoysk.funnyguilds.util;

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
            exception.printStackTrace();
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
            ioException.printStackTrace();
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
            ioException.printStackTrace();
        }
    }

}
