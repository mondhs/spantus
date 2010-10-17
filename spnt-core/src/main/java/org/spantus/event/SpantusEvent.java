package org.spantus.event;

import java.util.EventObject;

public class SpantusEvent extends EventObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String cmd; 
	private  Object value;
	
	public SpantusEvent(Object source, String cmd, Object value) {
		super(source);
		this.value = value;
		this.cmd = cmd;	
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public static SpantusEvent createEvent(Object source, String cmd){
		return new SpantusEvent(source, cmd, null);
	}
	public static SpantusEvent createEvent(Object source, String cmd, Object value){
		return new SpantusEvent(source, cmd, value);
	}

        public static SpantusEvent createEvent(Object source, Enum<?> cmd, Object value){
		return createEvent(source, cmd.name(), value);
	}

	
}
