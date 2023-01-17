package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.DetailedExceptionHandler;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;

class FunnyGuildsExceptionHandler implements DetailedExceptionHandler<Exception> {

    private final FunnyGuildsLogger logger;

    FunnyGuildsExceptionHandler(FunnyGuildsLogger logger) {
        this.logger = logger;
    }

    @Override
    public Class<Exception> getExceptionType() {
        return Exception.class;
    }

    @Override
    public Boolean apply(Context context, Exception ex) {
        this.logger.error("An exception has been caught while executing the command", ex);
        return true;
    }

}
