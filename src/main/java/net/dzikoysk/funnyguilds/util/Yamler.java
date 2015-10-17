package net.dzikoysk.funnyguilds.util;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Yamler extends YamlConfiguration {

    private File file;

    public Yamler(File file) {
        super();
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            super.load(file);
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            super.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
