package net.dzikoysk.funnyguilds.data.flat;

import java.io.File;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;

public final class FlatPatcher {

    private FlatPatcher() {
    }

    public static void patch(FunnyGuilds plugin, File usersFolder, File guildsFolder, File regionsFolder) {
        File users = new File(plugin.getDataFolder() + File.separator + "users");
        File guilds = new File(plugin.getDataFolder() + File.separator + "guilds");
        File regions = new File(plugin.getDataFolder() + File.separator + "regions");

        boolean usersExists = users.exists();
        boolean guildsExists = guilds.exists();
        boolean regionsExists = regions.exists();

        if (usersExists || guildsExists || regionsExists) {
            FunnyGuilds.getPluginLogger().update("Updating flat files...");
            FunnyGuilds.getPluginLogger().update("Scanning files...");
            int filesFound = 0;

            File[] usersList = users.listFiles();
            File[] guildsList = guilds.listFiles();
            File[] regionsList = regions.listFiles();

            filesFound += usersList != null ? usersList.length : 0;
            filesFound += guildsList != null ? guildsList.length : 0;
            filesFound += regionsList != null ? regionsList.length : 0;

            FunnyGuilds.getPluginLogger().update(filesFound + " files found...");
            FunnyGuilds.getPluginLogger().update("Updating files...");

            if (usersExists && !users.renameTo(usersFolder)) {
                FunnyGuilds.getPluginLogger().error("Failed to rename old users folder");
            }

            if (guildsExists && !guilds.renameTo(guildsFolder)) {
                FunnyGuilds.getPluginLogger().error("Failed to rename old guilds folder");
            }

            if (regionsExists && !regions.renameTo(regionsFolder)) {
                FunnyGuilds.getPluginLogger().error("Failed to rename old regions folder");
            }

            usersList = users.listFiles();
            guildsList = guilds.listFiles();
            regionsList = regions.listFiles();

            if (usersList == null || usersList.length == 0) {
                FunnyIOUtils.deleteFile(users);
            }

            if (guildsList == null || guildsList.length == 0) {
                FunnyIOUtils.deleteFile(guilds);
            }

            if (regionsList == null || regionsList.length == 0) {
                FunnyIOUtils.deleteFile(regions);
            }

            FunnyGuilds.getPluginLogger().update("Done!");
            FunnyGuilds.getPluginLogger().update("Updated files: " + filesFound);
        }
    }

}
