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
package org.spantus.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public class MapComboBoxModel<K,V> extends DefaultComboBoxModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<K, V> objectMap;
	private K selectedObject;

	/**
	 * use add instead
	 */
	@Deprecated
	public void addElement(Object obj) {
//		if(obj instanceof Entry<?,?>){
//			Entry<K, V> entry = (Entry<K,V>)obj;
//			getObjectMap().put(entry.getKey(), entry.getValue());
//		}else{
			throw new IllegalArgumentException(obj + " is not supported");
//		}
	}
	public void add(Entry<K,V> entry) {
		getObjectMap().put(entry.getKey(), entry.getValue());
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

	public K getSelectedItem() {
//		Object value = getObjectMap().get(selectedKey);
//		if(value == null){
//			return null;
//		}
//		ModelEntry entry = new ModelEntry(selectedKey, value);
		return selectedObject;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(Object anItem) {
		selectedObject = (K)anItem;
	}
	
	public void setSelectedObject(Object obj) {
		setSelectedItem(getLabel(obj));
	}

	public V get(String key){
		return getObjectMap().get(key);
	}
	
	public K getLabel(Object obj){
		for (Entry<K, V> entry : getObjectMap().entrySet()) {
			if(entry.getValue().equals(obj)){
				return entry.getKey();
			}
		}
		return null;
	}
	
	public V getSelectedObject(){
		return get((String)getSelectedItem());
	}
	
	public K getElementAt(int index) {
		int i = 0;
		for (Entry<K, V> entry : getObjectMap().entrySet()) {
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

	public Map<K, V> getObjectMap() {
		if(objectMap==null){
			objectMap = new LinkedHashMap<K, V>();
		}
		return objectMap;
	}

	public void setObjectMap(Map<K, V> objectMap) {
		this.objectMap = objectMap;
	}
	
}
