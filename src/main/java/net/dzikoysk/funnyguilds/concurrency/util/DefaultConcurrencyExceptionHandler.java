package net.dzikoysk.funnyguilds.concurrency.util;

import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyExceptionHandler;

public class DefaultConcurrencyExceptionHandler implements ConcurrencyExceptionHandler {

    @Override
    public void handleException(Exception exception) {
        FunnyLogger.exception(exception);
    }

}
