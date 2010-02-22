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

import java.util.Comparator;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public class ModelEntryByNameComparator implements Comparator<ModelEntry> {

	public int compare(ModelEntry o1, ModelEntry o2) {
		if(o1 == null && o2 == null){
			return 0;
		}
		if(o1 == null ){
			return -1;
		}
		if(o2 == null){
			return 1;
		}
		if(o1.getValue() == null && o2 == o1.getValue()){
			return 0;
		}
		if(o1.getValue() == null ){
			return -1;
		}
		if(o2.getValue() == null){
			return 1;
		}
		return o1.getKey().toString().compareTo(o2.getKey().toString());
	}

}
