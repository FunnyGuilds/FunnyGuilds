package net.dzikoysk.funnyguilds.concurrency.util;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyExceptionHandler;

public class DefaultConcurrencyExceptionHandler implements ConcurrencyExceptionHandler {

    @Override
    public void handleException(Exception exception) {
        FunnyGuildsLogger.exception(exception);
    }

}
