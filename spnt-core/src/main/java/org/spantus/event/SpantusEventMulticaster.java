package org.spantus.event;

public interface SpantusEventMulticaster {
	public void addListener(SpantusEventListener listener);
	public void multicastEvent(SpantusEvent event);
	public void removeAllListeners();
	public void removeListener(SpantusEventListener listener);
}
