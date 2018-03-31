package net.dzikoysk.funnyguilds.concurrency.util;

import net.dzikoysk.funnyguilds.concurrency.ConcurrencyExceptionHandler;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;

public abstract class DefaultConcurrencyTask implements ConcurrencyTask {

    protected ConcurrencyExceptionHandler exceptionHandler = new DefaultConcurrencyExceptionHandler();

    @Override
    public final void setExceptionHandler(ConcurrencyExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public boolean isMuted() {
        return true;
    }

    @Override
    public ConcurrencyExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public static ConcurrencyTask of(Runnable runnable) {
        return new DefaultConcurrencyTask() {
            @Override
            public void execute() throws Exception {
                runnable.run();
            }
        };
    }

}
