package org.spantus.work.ui.services.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;
import org.spantus.work.ui.services.MarkerLabeling;

public class MarkerLabelingProxyImpl implements MarkerLabeling{

	private Collection<MarkerLabeling> markerLabelings;
	private Set<ProcessedFrameLinstener> listeners;
	
	public MarkerLabelingProxyImpl(ProcessedFrameLinstener listener) {
		getListeners().add(listener);
	}
	
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
			MarkerLabelingRecognitionImpl recognition = new MarkerLabelingRecognitionImpl();
			markerLabelings.add(recognition);
		}
		return markerLabelings;
	}

	public void setMarkerLabelings(Collection<MarkerLabeling> markerLabelings) {
		this.markerLabelings = markerLabelings;
	}
	
	public Set<ProcessedFrameLinstener> getListeners() {
		if(listeners == null){
			listeners = new LinkedHashSet<ProcessedFrameLinstener>();
		}
		return listeners;
	}

	@Override
	public void update(SpantusWorkProjectInfo project,
			ProcessedFrameLinstener listener) {
		for (MarkerLabeling labelServices : getMarkerLabelings()) {
			labelServices.update(project, listener);
		}
//		recognition.getMatchingService().getListeners().addAll(getListeners());
		
	}

}
