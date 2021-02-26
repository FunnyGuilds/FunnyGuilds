package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.commons.IOUtils;

import java.io.File;

public class FlatPatcher {

    public void patch(FlatDataModel flatDataModel) {
        File guilds = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "guilds");
        File regions = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "regions");

        boolean guildsExists = guilds.exists();
        boolean regionsExists = regions.exists();

        if (guildsExists || regionsExists) {
            FunnyGuilds.getInstance().getPluginLogger().update("Updating flat files ...");
            FunnyGuilds.getInstance().getPluginLogger().update("Scanning files ...");
            int filesFound = 0;

            File[] guildsList = guilds.listFiles();
            File[] regionsList = regions.listFiles();

            filesFound += guildsList != null ? guildsList.length : 0;
            filesFound += regionsList != null ? regionsList.length : 0;

            FunnyGuilds.getInstance().getPluginLogger().update(filesFound + " files found ...");
            FunnyGuilds.getInstance().getPluginLogger().update("Updating files ...");

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

            FunnyGuilds.getInstance().getPluginLogger().update("Done!");
            FunnyGuilds.getInstance().getPluginLogger().update("Updated files: " + filesFound);
        }
    }

}
