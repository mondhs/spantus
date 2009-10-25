/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.02.29
 * 
 */
public class Logger implements ILogger {
	ILogger logger;

	public Logger(ILogger logger) {
		this.logger = logger;
	}

	public static Logger getLogger(Class<?> logClass) {
		ILogger logger1 = null;
		try {
			Class<?> loggingClass = Class.forName("org.spantus.work.ui.logger.LoggerLog4j");
			Constructor<?> loggerConstructor = loggingClass.getConstructor(Class.class);
			logger1 = (ILogger)loggerConstructor.newInstance(logClass);
		} catch (ClassNotFoundException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}finally{
			logger1 = logger1 ==null?new SimpleLogger(logClass):logger1;
		}
		
		Logger thisLogger = new Logger(logger1);
		return thisLogger;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.spantus.logger.ILogger#debug(java.lang.String, java.lang.Object)
	 */
	public void debug(String pattern, Object... arguments) {
		logger.debug(pattern, arguments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.spantus.logger.ILogger#debug(java.lang.String)
	 */
	public void debug(String str) {
		logger.debug(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.spantus.logger.ILogger#info(java.lang.String, java.lang.Object)
	 */
	public void info(String pattern, Object... arguments) {
		logger.info(pattern, arguments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.spantus.logger.ILogger#info(java.lang.String)
	 */
	public void info(String str) {
		logger.info(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.spantus.logger.ILogger#error(java.lang.String)
	 */
	public void error(String str) {
		logger.error(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.spantus.logger.ILogger#error(java.lang.Exception)
	 */
	public void error(Exception e) {
		logger.error(e);
	}

	public void fatal(String str) {
		logger.fatal(str);

	}

	public boolean isDebugMode() {
		return logger.isDebugMode();
	}

}
