package net.dzikoysk.funnyguilds.data.flat;

import java.io.File;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;

public final class FlatPatcher {

    private FlatPatcher() {
    }

    public static void patch(FunnyGuilds plugin, File guildsFolder, File regionsFolder) {
        File guilds = new File(plugin.getDataFolder() + File.separator + "guilds");
        File regions = new File(plugin.getDataFolder() + File.separator + "regions");

        boolean guildsExists = guilds.exists();
        boolean regionsExists = regions.exists();

        if (guildsExists || regionsExists) {
            FunnyGuilds.getPluginLogger().update("Updating flat files...");
            FunnyGuilds.getPluginLogger().update("Scanning files...");
            int filesFound = 0;

            File[] guildsList = guilds.listFiles();
            File[] regionsList = regions.listFiles();

            filesFound += guildsList != null ? guildsList.length : 0;
            filesFound += regionsList != null ? regionsList.length : 0;

            FunnyGuilds.getPluginLogger().update(filesFound + " files found...");
            FunnyGuilds.getPluginLogger().update("Updating files...");

            if (guildsExists && !guilds.renameTo(guildsFolder)) {
                FunnyGuilds.getPluginLogger().error("Failed to rename old guilds folder");
            }

            if (regionsExists && !regions.renameTo(regionsFolder)) {
                FunnyGuilds.getPluginLogger().error("Failed to rename old regions folder");
            }

            guildsList = guilds.listFiles();
            regionsList = regions.listFiles();

            if (guildsList == null || guildsList.length == 0) {
                FunnyIOUtils.delete(guilds);
            }

            if (regionsList == null || regionsList.length == 0) {
                FunnyIOUtils.delete(regions);
            }

            FunnyGuilds.getPluginLogger().update("Done!");
            FunnyGuilds.getPluginLogger().update("Updated files: " + filesFound);
        }
    }

}
