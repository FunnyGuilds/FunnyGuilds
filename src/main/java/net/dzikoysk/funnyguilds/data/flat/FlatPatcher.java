package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.commons.IOUtils;

import java.io.File;

public class FlatPatcher {

    public void patch(FlatDataModel flatDataModel) {
        File guilds = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "guilds");
        File regions = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "regions");

        boolean g = guilds.exists();
        boolean r = regions.exists();

        if (g || r) {
            FunnyGuilds.getInstance().getPluginLogger().update("Updating flat files ...");

            FunnyGuilds.getInstance().getPluginLogger().update("Scanning files ...");
            int todo = 0;
            int tg = 0;
            int tr = 0;

            File[] guildsList = guilds.listFiles();
            File[] regionsList = regions.listFiles();

            if (g && guildsList != null) {
                tg += guildsList.length;
            }
            if (r && regionsList != null) {
                tr += regionsList.length;
            }

            todo = tg + tr;
            FunnyGuilds.getInstance().getPluginLogger().update(todo + " files found ...");

            FunnyGuilds.getInstance().getPluginLogger().update("Updating files ...");
            if (g) {
                guilds.renameTo(flatDataModel.getGuildsFolder());
            }
            if (r) {
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
            FunnyGuilds.getInstance().getPluginLogger().update("Updated files: " + todo);
        }
    }

}
