package org.spantus.event;

import java.util.HashSet;
import java.util.Set;

public class BasicSpantusEventMulticaster implements SpantusEventMulticaster {
	
	private Set<SpantusEventListener> listeners ;
	
	public void addListener(SpantusEventListener listener) {
		getListeners().add(listener);
	}

	public void multicastEvent(SpantusEvent event) {
		Set<SpantusEventListener> listenersTmp = null;
		synchronized (listeners) {
			listenersTmp = 
				new HashSet<SpantusEventListener>(getListeners());
		}
		for (SpantusEventListener listener : listenersTmp) {
			listener.onEvent(event);
		}
		
	}

	public void removeAllListeners() {
		getListeners().clear();
	}

	public void removeListener(SpantusEventListener listener) {
		getListeners().remove(listener);
	}

	public Set<SpantusEventListener> getListeners() {
		if(listeners == null){
			listeners = new HashSet<SpantusEventListener>();
		}
		return listeners;
	}

}
