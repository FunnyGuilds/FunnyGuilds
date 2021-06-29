package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.resources.ExceptionHandler;
import net.dzikoysk.funnyguilds.FunnyGuilds;

class FunnyGuildsExceptionHandler implements ExceptionHandler<Exception> {
    private final FunnyGuilds funnyGuilds;

    public FunnyGuildsExceptionHandler(FunnyGuilds funnyGuilds) {
        this.funnyGuilds = funnyGuilds;
    }

    @Override
    public Class getExceptionType() {
        return Exception.class;
    }

    @Override
    public Boolean apply(Exception o) {
        funnyGuilds.getPluginLogger().error("An exception has been caught while executing the command", o);
        return true;
    }
}
