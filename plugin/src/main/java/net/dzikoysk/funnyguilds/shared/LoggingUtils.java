package net.dzikoysk.funnyguilds.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;

public final class LoggingUtils {

    private LoggingUtils() {}

    public static void flushRootLogger() {
        LoggingUtils.flushLogger((Logger) LogManager.getRootLogger());
    }

    public static void flushLogger(Logger rootLogger) {
        rootLogger.getAppenders().values()
                  .stream()
                  .filter(appender -> appender instanceof AbstractOutputStreamAppender)
                  .map(appender -> (AbstractOutputStreamAppender) appender)
                  .filter(appender -> ! appender.getImmediateFlush())
                  .forEach(appender -> appender.getManager().flush());
    }

}
