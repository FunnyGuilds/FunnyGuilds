package net.dzikoysk.funnyguilds.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyManager {

    private final ExecutorService executor;

    public ConcurrencyManager(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public void postRequests(ConcurrencyRequest... requests) {
        ConcurrencyTask task = new ConcurrencyTask(requests);
        this.executor.submit(task);
    }

}
