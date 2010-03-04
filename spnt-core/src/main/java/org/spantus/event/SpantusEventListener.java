package org.spantus.event;

import java.util.EventListener;

public interface SpantusEventListener extends EventListener {
	public void onEvent(SpantusEvent event);
}
