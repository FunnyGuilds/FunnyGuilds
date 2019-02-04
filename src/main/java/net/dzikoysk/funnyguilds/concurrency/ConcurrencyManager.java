package net.dzikoysk.funnyguilds.concurrency;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyManager {

    private final int threads;
    private final ExecutorService executor;

    public ConcurrencyManager(int threads) {
        this.threads = threads;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public void postRequests(ConcurrencyRequest... requests) {
        ConcurrencyTask task = new ConcurrencyTask(requests);
        this.postTask(task);
    }

    public void postTask(ConcurrencyTask task) {
        this.executor.submit(task);
    }

    public void printStatus() {
        FunnyGuilds.getInstance().getPluginLogger().info("Available Processors: " + Runtime.getRuntime().availableProcessors());
        FunnyGuilds.getInstance().getPluginLogger().info("Active Threads: " + Thread.activeCount());
        FunnyGuilds.getInstance().getPluginLogger().info("Pool size: " + threads);
    }

}
