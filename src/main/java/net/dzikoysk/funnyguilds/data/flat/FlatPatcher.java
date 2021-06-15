package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.util.YamlWrapper;
import net.dzikoysk.funnyguilds.util.commons.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FlatPatcher {

    public void patch(FlatDataModel flatDataModel) {
        File guilds = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "guilds");
        File regions = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "regions");

        boolean guildsExists = guilds.exists();
        boolean regionsExists = regions.exists();

        patchUsers(flatDataModel.getUsersFolder());

        if (guildsExists || regionsExists) {
            FunnyGuilds.getPluginLogger().update("Updating flat files ...");
            FunnyGuilds.getPluginLogger().update("Scanning files ...");
            int filesFound = 0;

            File[] guildsList = guilds.listFiles();
            File[] regionsList = regions.listFiles();

            filesFound += guildsList != null ? guildsList.length : 0;
            filesFound += regionsList != null ? regionsList.length : 0;

            FunnyGuilds.getPluginLogger().update(filesFound + " files found ...");
            FunnyGuilds.getPluginLogger().update("Updating files ...");

            if (guildsExists) {
                guilds.renameTo(flatDataModel.getGuildsFolder());
            }

            if (regionsExists) {
                regions.renameTo(flatDataModel.getRegionsFolder());
            }

            guildsList = guilds.listFiles();
            regionsList = regions.listFiles();

            if (guildsList == null || guildsList.length == 0) {
                IOUtils.delete(guilds);
            }
            if (regionsList == null || regionsList.length == 0) {
                IOUtils.delete(regions);
            }

            FunnyGuilds.getPluginLogger().update("Done!");
            FunnyGuilds.getPluginLogger().update("Updated files: " + filesFound);
        }
    }

    private void patchUsers(File usersFolder) {
        File[] files = usersFolder.listFiles((dir, name) -> UserUtils.validateUsername(StringUtils.removeEnd(name, ".yml")));

        if (files == null || files.length == 0) {
            return;
        }

        File usersFolderCopy = new File(usersFolder.getPath() + "-copy");
        usersFolderCopy.mkdirs();

        for (File file : files) {
            YamlWrapper wrapper = new YamlWrapper(file);

            String id = wrapper.getString("uuid");

            if (id == null || !UserUtils.validateUUID(id)) {
                continue;
            }

            try {
                Files.copy(file.toPath(), new File(usersFolderCopy, id + ".yml").toPath());
            } catch (IOException e) {
                throw new RuntimeException("Could not patch user '" + id + "'", e);
            }
        }

        IOUtils.delete(usersFolder);
        usersFolderCopy.renameTo(usersFolder);
    }

}
