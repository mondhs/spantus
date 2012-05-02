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
import java.util.Map.Entry;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1 Created Apr 2, 2009
 * 
 */
public class ModelEntry<K, V> implements Entry<K, V> {
	private K key;
	private V value;
	private Integer order = 0;

	public ModelEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public ModelEntry(Entry<K, V> entry) {
		this.key = entry.getKey();
		this.value = entry.getValue();
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public V setValue(V value) {
		this.value = value;
		return value;
	}

	public void setKey(K key) {
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
		return getKey().toString();
	}


}
