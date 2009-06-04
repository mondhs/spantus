package org.spantus.ui;
import java.util.Map.Entry;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1 Created Apr 2, 2009
 * 
 */
public class ModelEntry implements Entry<String, Object> {
	private String key;
	private Object value;
	private Integer order = 0;

	public ModelEntry(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public ModelEntry(Entry<String, Object> entry) {
		this.key = entry.getKey();
		this.value = entry.getValue();
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public Object setValue(Object value) {
		this.value = value;
		return value;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return getKey();
	}


}
