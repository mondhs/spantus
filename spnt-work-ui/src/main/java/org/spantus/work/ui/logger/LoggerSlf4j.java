package org.spantus.work.ui.logger;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.logger.ILogger;


public class LoggerSlf4j implements ILogger{
	private Logger logger;
	
	public LoggerSlf4j(Class<?> logClass){
	    this.logger = LoggerFactory.getLogger(logClass);
	}

	public void debug(String pattern, Object... arguments) {
		debug(MessageFormat.format(pattern, arguments));
	}

	public void debug(String str) {
		logger.debug(str);
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
        @Override
        public void error(String pattern, Object... arguments) {
            logger.error(MessageFormat.format(pattern, arguments));
        }
	
	public void fatal(String str) {
		logger.error(str);
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
