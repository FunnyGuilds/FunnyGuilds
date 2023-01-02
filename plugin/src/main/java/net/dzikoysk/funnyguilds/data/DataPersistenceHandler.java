package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.DataSaveAsyncTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class DataPersistenceHandler {

    private final FunnyGuilds plugin;
    private volatile BukkitTask dataPersistenceHandlerTask;

    public DataPersistenceHandler(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public void startHandler() {
        long interval = this.plugin.getPluginConfiguration().dataInterval * 60L * 20L;

        if (this.dataPersistenceHandlerTask != null) {
            this.dataPersistenceHandlerTask.cancel();
        }

        this.dataPersistenceHandlerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            this.plugin.scheduleFunnyTasks(new DataSaveAsyncTask(this.plugin.getDataModel(), false));
        }, interval, interval);
    }

    public void stopHandler() {
        if (this.dataPersistenceHandlerTask == null) {
            return;
        }

        this.dataPersistenceHandlerTask.cancel();
        this.dataPersistenceHandlerTask = null;
    }

    public void reloadHandler() {
        this.stopHandler();
        this.startHandler();
    }

}
