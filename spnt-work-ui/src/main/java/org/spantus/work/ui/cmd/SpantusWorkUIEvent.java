package org.spantus.work.ui.cmd;

import org.spantus.event.SpantusEvent;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class SpantusWorkUIEvent extends SpantusEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SpantusWorkInfo ctx;

	
	public SpantusWorkUIEvent(Object source, SpantusWorkInfo ctx, String cmd, Object value) {
		super(source, cmd, value);
		this.ctx = ctx;
	}

	public SpantusWorkInfo getCtx() {
		return ctx;
	}
}
