package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.requests.DataSaveRequest;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class DataPersistenceHandler {

    private final    FunnyGuilds funnyGuilds;
    private volatile BukkitTask  dataPersistenceHandlerTask;

    public DataPersistenceHandler(FunnyGuilds funnyGuilds) {
        this.funnyGuilds = funnyGuilds;
    }

    public void startHandler() {
        long interval = this.funnyGuilds.getPluginConfiguration().dataInterval * 60 * 20;

        if (this.dataPersistenceHandlerTask != null) {
            this.dataPersistenceHandlerTask.cancel();
        }

        this.dataPersistenceHandlerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.funnyGuilds,
                () -> this.funnyGuilds.getConcurrencyManager().postRequests(new DataSaveRequest(false)), interval, interval);
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
