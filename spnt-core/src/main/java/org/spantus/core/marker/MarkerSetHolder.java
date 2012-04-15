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
package org.spantus.core.marker;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Reperesent collection of different layers of segmentations as phones, words etc.
 * 
 * @author mondhs
 *
 */
public class MarkerSetHolder implements Serializable, Iterable<Map.Entry<String, MarkerSet>>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1670053945249372837L;
	private Map<String, MarkerSet> markerSets;

	/**
	 * Recommended marker set types
	 * 
	 * @author mondhs
	 *
	 */
	public enum MarkerSetHolderEnum{phone, syllable, word, sentence}
	
	/**
	 * key should be the same as {@link MarkerSet#getMarkerSetType()}. Recommended use {@link MarkerSetHolderEnum} for key values
	 * 
	 * @return
	 */
	public Map<String, MarkerSet> getMarkerSets() {
		if(markerSets == null){
			markerSets = new LinkedHashMap<String, MarkerSet>();
		}
		return markerSets;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0} [layers:{1}]", getClass().getName()
				, getMarkerSets().size());
	}

	@Override
	public Iterator<Entry<String, MarkerSet>> iterator() {
		return getMarkerSets().entrySet().iterator();
	}

}
