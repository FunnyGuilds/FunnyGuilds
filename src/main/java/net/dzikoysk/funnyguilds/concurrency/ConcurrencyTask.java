package net.dzikoysk.funnyguilds.concurrency;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;

import java.util.concurrent.atomic.AtomicInteger;

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
                FunnyGuildsLogger.warning("Task #" + id + " has been interrupted");
                return;
            }
        }
    }

    private boolean execute(ConcurrencyRequest request) {
        try {
            request.execute();
        } catch (Exception exception) {
            if (request.getExceptionHandler() != null) {
                request.getExceptionHandler().handleException(exception);
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
