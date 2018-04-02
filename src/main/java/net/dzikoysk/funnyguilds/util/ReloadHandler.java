package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.data.Data;
import org.bukkit.Bukkit;
import org.panda_lang.panda.utilities.configuration.PandaConfiguration;

import java.io.File;

public class ReloadHandler {

    private static final File DATA_FILE = new File(Data.getDataFolder(), "funnyguilds.dat");
    private static int reloadCount;

    // TODO: Does it work and what's the purpose?
    public void init() {
        PandaConfiguration pc = new PandaConfiguration(DATA_FILE);
        int before = pc.getInt("played-before");
        int actual = Bukkit.getOnlinePlayers().size();
        boolean reloaded = before == actual;

        if (reloaded) {
            reloadCount = pc.getInt("reload-count") + 1;
        }
    }

    public static int getReloadCount() {
        return reloadCount;
    }

}
