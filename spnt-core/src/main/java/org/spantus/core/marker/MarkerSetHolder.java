package org.spantus.core.marker;

import java.util.LinkedHashMap;
import java.util.Map;

public class MarkerSetHolder {
	
	Map<String, MarkerSet> markerSets;

	public enum MarkerSetHolderEnum{phone, word, sentence}
	
	public Map<String, MarkerSet> getMarkerSets() {
		if(markerSets == null){
			markerSets = new LinkedHashMap<String, MarkerSet>();
		}
		return markerSets;
	}

}
