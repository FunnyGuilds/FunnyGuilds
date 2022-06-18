package net.dzikoysk.funnyguilds.data.util;

import java.io.File;
import java.io.IOException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import panda.std.Result;

public final class YamlWrapper extends YamlConfiguration {

    private final File file;

    public YamlWrapper(File file) {
        Result<File, String> createResult = FunnyIOUtils.createFile(file, false);
        if (createResult.isErr()) {
            FunnyGuilds.getPluginLogger().error(createResult.getError());
            this.file = null;
            return;
        }

        this.file = createResult.get();

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
            Result<File, String> createResult = FunnyIOUtils.createFile(file, false);
            if (createResult.isErr()) {
                FunnyGuilds.getPluginLogger().error(createResult.getError());
                return;
            }

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
