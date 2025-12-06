package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.DataSaveAsyncTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicReference;

public class DataPersistenceHandler {

    private final FunnyGuilds plugin;
    private final AtomicReference<BukkitTask> dataPersistenceHandlerTask = new AtomicReference<>();

    public DataPersistenceHandler(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public void startHandler() {
        long interval = this.plugin.getPluginConfiguration().dataInterval * 60L * 20L;

        BukkitTask oldTask = this.dataPersistenceHandlerTask.getAndSet(
            Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
                this.plugin.scheduleFunnyTasks(new DataSaveAsyncTask(this.plugin.getDataModel(), false));
            }, interval, interval)
        );

        if (oldTask != null) {
            oldTask.cancel();
        }
    }

    public void stopHandler() {
        BukkitTask task = this.dataPersistenceHandlerTask.getAndSet(null);
        if (task != null) {
            task.cancel();
        }
    }

    public void reloadHandler() {
        this.stopHandler();
        this.startHandler();
    }

}
