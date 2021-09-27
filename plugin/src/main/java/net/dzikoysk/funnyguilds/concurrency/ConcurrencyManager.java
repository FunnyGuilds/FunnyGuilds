package net.dzikoysk.funnyguilds.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnyguilds.FunnyGuilds;

public class ConcurrencyManager {

    private final FunnyGuilds funnyGuilds;
    private final int threads;
    private final ExecutorService executor;

    public ConcurrencyManager(FunnyGuilds funnyGuilds, int threads) {
        this.funnyGuilds = funnyGuilds;
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

    public void awaitTermination(long timeout) {
        this.executor.shutdown();

        try {
            this.executor.awaitTermination(timeout, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex) {
            funnyGuilds.getPluginLogger().error("ConcurrencyManager termination failed", ex);
        }
    }

    public void printStatus() {
        funnyGuilds.getPluginLogger().info("Available Processors: " + Runtime.getRuntime().availableProcessors());
        funnyGuilds.getPluginLogger().info("Active Threads: " + Thread.activeCount());
        funnyGuilds.getPluginLogger().info("Pool size: " + threads);
    }

}
