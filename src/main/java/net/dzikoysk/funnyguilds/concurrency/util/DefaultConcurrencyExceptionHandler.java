package net.dzikoysk.funnyguilds.concurrency.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyExceptionHandler;

public class DefaultConcurrencyExceptionHandler implements ConcurrencyExceptionHandler {

    @Override
    public void handleException(Exception exception) {
        FunnyGuilds.getInstance().getPluginLogger().error("An error occurred while handling concurrent request", exception);
    }

}
