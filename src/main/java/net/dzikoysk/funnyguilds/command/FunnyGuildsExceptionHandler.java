package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.resources.ExceptionHandler;
import net.dzikoysk.funnyguilds.FunnyGuilds;

class FunnyGuildsExceptionHandler implements ExceptionHandler<Exception> {

    @Override
    public Class getExceptionType() {
        return Exception.class;
    }

    @Override
    public Boolean apply(Exception o) {
        FunnyGuilds.getPluginLogger().error("An exception has been caught while executing the command", o);
        return true;
    }
}
