package net.dzikoysk.funnyguilds.shared;

import net.dzikoysk.funnyguilds.FunnyGuilds;

public interface FunnyTask extends Runnable {

    enum Type {
        SYNC,
        ASYNC
    }

    abstract class AsyncFunnyTask implements FunnyTask {

        @Override
        public final Type getType() {
            return Type.ASYNC;
        }

    }

    abstract class SyncFunnyTask implements FunnyTask {

        @Override
        public final Type getType() {
            return Type.SYNC;
        }

    }

    @Override
    default void run() {
        try {
            execute();
        } catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("An error occurred while handling concurrent request", exception);
        }
    }

    void execute() throws Exception;

    Type getType();

}
