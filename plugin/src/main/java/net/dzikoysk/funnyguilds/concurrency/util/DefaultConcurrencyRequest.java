package net.dzikoysk.funnyguilds.concurrency.util;

import net.dzikoysk.funnyguilds.concurrency.ConcurrencyExceptionHandler;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyRequest;

public abstract class DefaultConcurrencyRequest implements ConcurrencyRequest {

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
        return this.exceptionHandler;
    }

    public static ConcurrencyRequest of(Runnable runnable) {
        return new DefaultConcurrencyRequest() {
            @Override
            public void execute() throws Exception {
                runnable.run();
            }
        };
    }

}
