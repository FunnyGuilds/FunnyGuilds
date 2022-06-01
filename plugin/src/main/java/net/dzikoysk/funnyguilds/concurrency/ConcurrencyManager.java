package net.dzikoysk.funnyguilds.concurrency;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnyguilds.FunnyGuilds;

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

    public void awaitTermination(Duration timeout) {
        this.executor.shutdown();

        try {
            this.executor.awaitTermination(timeout.getSeconds(), TimeUnit.SECONDS);
        }
        catch (InterruptedException ex) {
            FunnyGuilds.getPluginLogger().error("ConcurrencyManager termination failed", ex);
        }
    }

    public void printStatus() {
        FunnyGuilds.getPluginLogger().info("Available Processors: " + Runtime.getRuntime().availableProcessors());
        FunnyGuilds.getPluginLogger().info("Active Threads: " + Thread.activeCount());
        FunnyGuilds.getPluginLogger().info("Pool size: " + threads);
    }

}
