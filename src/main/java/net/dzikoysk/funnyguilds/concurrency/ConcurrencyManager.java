package net.dzikoysk.funnyguilds.concurrency;

import net.dzikoysk.funnyguilds.FunnyLogger;

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
        this.executor.submit(task);
    }

    public void printStatus() {
        FunnyLogger.info("Available Processors: " + Runtime.getRuntime().availableProcessors());
        FunnyLogger.info("Active Threads: " + Thread.activeCount());
        FunnyLogger.info("Pool size: " + threads);
    }

}
