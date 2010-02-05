package org.spantus.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;

public class MapComboBoxModel extends DefaultComboBoxModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, Object> objectMap;
	private String selectedObject;

	@SuppressWarnings("unchecked")
	public void addElement(Object obj) {
		if(obj instanceof Entry<?,?>){
			Entry<String, Object> entry = (Entry<String,Object>)obj;
			getObjectMap().put(entry.getKey(), entry.getValue());
		}else{
			throw new IllegalArgumentException(obj + " is not supported");
		}
	}

	public void insertElementAt(Object obj, int index) {
		throw new IllegalArgumentException("not implemented!");
	}

	public void removeElement(Object obj) {
		throw new IllegalArgumentException("not implemented!");
	}

	public void removeElementAt(int index) {
		throw new IllegalArgumentException("not implemented!");
	}

	public Object getSelectedItem() {
//		Object value = getObjectMap().get(selectedKey);
//		if(value == null){
//			return null;
//		}
//		ModelEntry entry = new ModelEntry(selectedKey, value);
		return selectedObject;
	}
	@Override
	public void setSelectedItem(Object anItem) {
		selectedObject = (String)anItem;
	}
	
	public void setSelectedObject(Object obj) {
		setSelectedItem(getLabel(obj));
	}

	public Object get(String key){
		return getObjectMap().get(key);
	}
	
	public String getLabel(Object obj){
		for (Entry<String, Object> entry : getObjectMap().entrySet()) {
			if(entry.getValue().equals(obj)){
				return entry.getKey();
			}
		}
		return null;
	}
	
	public Object getSelectedObject(){
		return get((String)getSelectedItem());
	}
	
	public Object getElementAt(int index) {
		int i = 0;
		for (Entry<String, Object> entry : getObjectMap().entrySet()) {
			i++;
			if(i == index){
				return entry.getKey();
			}
			
		}

		return null;
	}

	public int getSize() {
		return getObjectMap().size()+1;
	}

	public void removeListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}
	
//	@Override
//	public void addListDataListener(ListDataListener l) {
//		super.addListDataListener(l);
//		fireIntervalAdded(this, 0, getObjectMap().size()-1);
//	}

	public Map<String, Object> getObjectMap() {
		if(objectMap==null){
			objectMap = new LinkedHashMap<String, Object>();
		}
		return objectMap;
	}

	public void setObjectMap(Map<String, Object> objectMap) {
		this.objectMap = objectMap;
	}
	
}
