package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.data.Data;
import org.bukkit.Bukkit;
import org.panda_lang.panda.util.configuration.PandaConfiguration;

import java.io.File;

public class Reloader {

    private static boolean init;
    private static int reloadCount;
    private static boolean reloaded;

    public void init() {
        PandaConfiguration pc = new PandaConfiguration(new File(Data.getDataFolder(), "funnyguilds.dat"));
        int before = pc.getInt("played-before");
        int actual = Bukkit.getOnlinePlayers().length;
        reloaded = before == actual;

        if (reloaded) {
            reloadCount = pc.getInt("reload-count") + 1;
        }

        init = true;
    }

    public static int getReloadCount() {
        return reloadCount;
    }

    public static boolean wasReloaded() {
        if (!init) {
            Reloader reloader = new Reloader();
            reloader.init();
        }

        return reloaded;
    }

}
