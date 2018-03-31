package net.dzikoysk.funnyguilds.concurrency;

public interface ConcurrencyTask {

    void execute() throws Exception;

    void setExceptionHandler(ConcurrencyExceptionHandler exceptionHandler);

    boolean isMuted();

    ConcurrencyExceptionHandler getExceptionHandler();

}
