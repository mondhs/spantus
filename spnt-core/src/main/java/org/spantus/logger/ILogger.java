package org.spantus.logger;

public interface ILogger {
	
	public abstract void debug(String pattern, Object... arguments);

	public abstract void debug(String str);

	public abstract void info(String pattern, Object... arguments);

	public abstract void info(String str);

	public abstract void error(String str);

	public abstract void error(Exception e);

	public abstract void fatal(String str);

	public abstract boolean isDebugMode();

}