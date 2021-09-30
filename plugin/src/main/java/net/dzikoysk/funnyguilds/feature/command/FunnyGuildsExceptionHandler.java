package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.ExceptionHandler;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;

class FunnyGuildsExceptionHandler implements ExceptionHandler<Exception> {

    private final FunnyGuildsLogger logger;

    FunnyGuildsExceptionHandler(FunnyGuildsLogger logger) {
        this.logger = logger;
    }

    @Override
    public Class getExceptionType() {
        return Exception.class;
    }

    @Override
    public Boolean apply(Exception o) {
        this.logger.error("An exception has been caught while executing the command", o);
        return true;
    }

}
