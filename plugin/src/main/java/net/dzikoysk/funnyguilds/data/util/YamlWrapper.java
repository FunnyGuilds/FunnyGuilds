package net.dzikoysk.funnyguilds.data.util;

import java.io.File;
import java.io.IOException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public final class YamlWrapper extends YamlConfiguration {

    private final File file;

    public YamlWrapper(File file) {
        this.file = FunnyIOUtils.createFile(file, false);

        try {
            super.load(this.file);
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Failed to load YAML file", exception);
        }
    }

    @Override
    public void save(@NotNull File file) {
        try {
            FunnyIOUtils.createFile(file, false);
            super.save(file);
        }
        catch (IOException exception) {
            FunnyGuilds.getPluginLogger().error("Failed to save YAML file", exception);
        }
    }

    public void save() {
        this.save(this.file);
    }

}
