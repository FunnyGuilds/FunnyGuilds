package net.dzikoysk.funnyguilds.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import net.dzikoysk.funnyguilds.FunnyGuilds;

public class ConcurrencyTask implements Runnable {

    private static final AtomicInteger idAssigner = new AtomicInteger();

    private final int id;
    private final ConcurrencyRequest[] requests;

    protected ConcurrencyTask(ConcurrencyRequest... requests) {
        this.id = idAssigner.getAndIncrement();
        this.requests = requests;
    }

    @Override
    public void run() {
        for (ConcurrencyRequest request : requests) {
            boolean result = execute(request);

            if (!result) {
                FunnyGuilds.getPluginLogger().warning("Task #" + id + " has been interrupted");
                return;
            }
        }
    }

    private static boolean execute(ConcurrencyRequest request) {
        try {
            request.execute();
        }
        catch (Throwable throwable) {
            if (request.getExceptionHandler() != null) {
                request.getExceptionHandler().handleException(throwable);
            }

            if (!request.isMuted()) {
                return false;
            }
        }

        return true;
    }

    public static ConcurrencyTaskBuilder builder() {
        return new ConcurrencyTaskBuilder();
    }

}
