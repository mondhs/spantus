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

/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public class ModelEntryByOrderComparator<K,V>  extends ModelEntryByNameComparator<K,V>{

	public int compare(ModelEntry<K,V> o1, ModelEntry<K,V> o2) {
		if(o1 == null && o2 == null){
			return 0;
		}
		if(o1 == null ){
			return -1;
		}
		if(o2 == null){
			return 1;
		}
		if(o1.getOrder() == null && o2.getOrder() == null){
			return 0;
		}
		if(o1.getOrder() == null ){
			return -1;
		}
		if(o2.getOrder() == null){
			return 1;
		}
		int compare = o1.getOrder().compareTo(o2.getOrder());
		compare = compare == 0?super.compare(o1, o2):compare;
		return compare;
	}

}
