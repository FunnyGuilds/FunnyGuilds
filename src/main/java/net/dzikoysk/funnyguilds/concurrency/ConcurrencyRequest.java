package net.dzikoysk.funnyguilds.concurrency;

import net.dzikoysk.funnyguilds.FunnyLogger;

import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyRequest implements Runnable {

    private static final AtomicInteger idAssigner = new AtomicInteger();

    private final int id;
    private final ConcurrencyTask[] tasks;

    public ConcurrencyRequest(ConcurrencyTask... tasks) {
        this.id = idAssigner.getAndIncrement();
        this.tasks = tasks;
    }

    @Override
    public void run() {
        for (ConcurrencyTask task : tasks) {
            boolean result = execute(task);

            if (!result) {
                FunnyLogger.warning("Request #" + id + " has been interrupted");
                return;
            }
        }
    }

    private boolean execute(ConcurrencyTask task) {
        try {
            task.execute();
        } catch (Exception exception) {
            if (task.getExceptionHandler() != null) {
                task.getExceptionHandler().handleException(exception);
            }

            if (!task.isMuted()) {
                return false;
            }
        }

        return true;
    }

}
