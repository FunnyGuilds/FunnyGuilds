package net.dzikoysk.funnyguilds.concurrency;

public interface ConcurrencyRequest {

    void execute() throws Exception;

    void setExceptionHandler(ConcurrencyExceptionHandler exceptionHandler);

    boolean isMuted();

    ConcurrencyExceptionHandler getExceptionHandler();

}
