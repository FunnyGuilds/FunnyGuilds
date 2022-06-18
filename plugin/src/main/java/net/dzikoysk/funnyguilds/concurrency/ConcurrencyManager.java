package net.dzikoysk.funnyguilds.concurrency;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;

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
            if (!this.executor.awaitTermination(timeout.getSeconds(), TimeUnit.SECONDS)) {
                this.executor.shutdownNow();
            }
        }
        catch (InterruptedException exception) {
            FunnyGuilds.getPluginLogger().error("ConcurrencyManager termination failed", exception);
        }
    }

    public void printStatus() {
        FunnyGuildsLogger logger = FunnyGuilds.getPluginLogger();

        logger.info("Available Processors: " + Runtime.getRuntime().availableProcessors());
        logger.info("Active Threads: " + Thread.activeCount());
        logger.info("Pool size: " + this.threads);
    }

}
