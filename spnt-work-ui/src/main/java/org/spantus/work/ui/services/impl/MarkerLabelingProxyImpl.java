package org.spantus.work.ui.services.impl;

import java.util.Collection;
import java.util.LinkedList;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.services.MarkerLabeling;

public class MarkerLabelingProxyImpl implements MarkerLabeling{

	private Collection<MarkerLabeling> markerLabelings;
	
	public MarkerSetHolder label(MarkerSetHolder markerSetHolder, SpantusWorkInfo ctx, IExtractorInputReader reader) {
		for (MarkerLabeling markerLabeling : getMarkerLabelings()) {
			MarkerSetHolder msh = markerLabeling.label(markerSetHolder, ctx, reader);
			if(msh != null){
				return msh;
			}
		}
		return null;
	}

	public Collection<MarkerLabeling> getMarkerLabelings() {
		if(markerLabelings == null){
			markerLabelings = new LinkedList<MarkerLabeling>();
			markerLabelings.add(new MarkerLabelingBaseImpl());
			markerLabelings.add(new MarkerLabelingTextGridImpl());
			markerLabelings.add(new MarkerLabelingRecognitionImpl());
		}
		return markerLabelings;
	}

	public void setMarkerLabelings(Collection<MarkerLabeling> markerLabelings) {
		this.markerLabelings = markerLabelings;
	}

}
