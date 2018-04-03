package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.DataSaveRequest;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.database.DatabaseBasic;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public class Manager {

    private static Manager instance;
    private volatile BukkitTask task = null;

    public Manager() {
        instance = this;
        Messages.getInstance();
        Settings.getConfig();
        
        if (Settings.getConfig().dataType.mysql) {
            DatabaseBasic.getInstance().load();
        } else {
            Flat.getInstance().load();
        }
        
        Data.getInstance();
    }

    public static Manager getInstance() {
        if (instance != null) {
            return instance;
        }
        
        new Manager().start();
        return instance;
    }

    public static void loadDefaultFiles(String[] files) {
        for (String file : files) {
            File cfg = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + file);
            if (!cfg.exists()) {
                FunnyGuilds.getInstance().saveResource(file, true);
            }
        }
    }

    public void save() {
        if (Settings.getConfig().dataType.flat) {
            try {
                Flat.getInstance().save(false);
            } catch (Exception e) {
                FunnyLogger.error("An error occurred while saving data to flat file! Caused by: Exception");
                if (FunnyLogger.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }
        
        if (Settings.getConfig().dataType.mysql) {
            try {
                DatabaseBasic.getInstance().save(false);
            } catch (Exception e) {
                FunnyLogger.error("An error occurred while saving data to database! Caused by: Exception");
                if (FunnyLogger.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }
        
        Data.getInstance().save();
    }

    public void start() {
        if (FunnyGuilds.getInstance().isDisabling()) {
            return;
        }
        
        if (this.task != null) {
            return;
        }

        FunnyGuilds funnyGuilds = FunnyGuilds.getInstance();
        ConcurrencyManager concurrencyManager = funnyGuilds.getConcurrencyManager();

        long interval = this.getSettings().dataInterval * 60 * 20;
        DataSaveRequest saveRequest = new DataSaveRequest();
        
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(funnyGuilds, () -> concurrencyManager.postRequests(saveRequest), interval, interval);
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    public PluginConfig getSettings() {
        return Settings.getConfig();
    }

    public MessagesConfig getMessages() {
        return Messages.getInstance();
    }
}
