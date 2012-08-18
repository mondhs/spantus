/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
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
@SuppressWarnings("unchecked")
public class Logger implements ILogger {
	ILogger logger;

	public Logger(ILogger logger) {
		this.logger = logger;
	}

	private static Class<ILogger> loggerClass;

	static {
		String[] clazzes = new String[] {
				"org.spantus.work.ui.logger.LoggerLog4j",
				"org.spantus.android.SpntAndroidLogger",
				"org.spantus.logger.SimpleLogger" };
		ILogger logger1 = null;
		for (String clazz : clazzes) {
			try {

				Class<?> loggingClass = Class.forName(clazz);
				Constructor<?> loggerConstructor = loggingClass
						.getConstructor(Class.class);
				logger1 = (ILogger) loggerConstructor.newInstance(loggingClass);
			} catch (ClassNotFoundException e) {
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			} catch (IllegalArgumentException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			if (logger1 != null) {
				loggerClass = (Class<ILogger>) logger1.getClass();
				break;
			}

		}

	}


	public static Logger getLogger(Class<?> logClass) {
		try {
			Constructor<?> loggerConstructor = loggerClass
					.getConstructor(Class.class);
			ILogger logger1 = (ILogger) loggerConstructor.newInstance(logClass);
			Logger thisLogger = new Logger(logger1);
			return thisLogger;
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} finally {
		}
		return null;
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
	 * @see org.spantus.logger.ILogger#error(java.lang.String,
	 * java.lang.Throwable)
	 */
	public void error(String str, Throwable t) {
		logger.error(str, t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.spantus.logger.ILogger#error(java.lang.Exception)
	 */
	public void error(Exception e) {
		logger.error(e);
	}
        @Override
        public void error(String pattern, Object... arguments) {
            logger.error(pattern, arguments);
        }

	public void fatal(String str) {
		logger.fatal(str);

	}

	public boolean isDebugMode() {
		return logger.isDebugMode();
	}



}
