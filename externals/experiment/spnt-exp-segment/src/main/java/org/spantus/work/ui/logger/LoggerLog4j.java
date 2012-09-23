package org.spantus.work.ui.logger;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.spantus.logger.ILogger;

public class LoggerLog4j implements ILogger {

    Logger logger;

    public LoggerLog4j(Class<?> logClass) {
        this.logger = Logger.getLogger(logClass);
    }

    public void debug(String pattern, Object... arguments) {
        debug(MessageFormat.format(pattern, arguments));
    }

    public void debug(String str) {
        logger.debug(str);
    }

    @Override
    public void error(String pattern, Object... arguments) {
         logger.error(MessageFormat.format(pattern, arguments));
    }

    public void error(String str) {
        logger.error(str);
    }

    public void error(Exception e) {
        logger.error(e.getMessage(), e);
    }

    public void error(String str, Throwable e) {
        logger.error(str, e);
    }

    public void fatal(String str) {
        logger.fatal(str);
    }

    public void info(String pattern, Object... arguments) {
        info(MessageFormat.format(pattern, arguments));

    }

    public void info(String str) {
        logger.info(str);

    }

    public boolean isDebugMode() {
        return logger.isDebugEnabled();
    }
}
