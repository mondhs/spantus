package org.spantus.work.ui.services.impl;

import java.util.Map;

import org.spantus.core.IValues;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;
import org.spantus.work.ui.services.MarkerLabeling;

public class MarkerLabelingRecognitionImpl implements MarkerLabeling{

	private WorkExtractorReaderService extractorReaderService;
	private MatchingServiceImpl matchingService;
	
	
	public void update(SpantusWorkProjectInfo project, ProcessedFrameLinstener listener){
		getMatchingService().update(project.getRecognitionConfig(), listener);
	}
	
	public MarkerSetHolder label(MarkerSetHolder markerSetHolder, SpantusWorkInfo ctx, IExtractorInputReader reader) {
		
		boolean autoRecognize = (Boolean.TRUE.equals(ctx.getEnv()
				.getAutoRecognition()));
		if(!autoRecognize){
			return null;
		}
		return putLabelsRecognized(reader, markerSetHolder);
	}

	public MarkerSetHolder putLabelsRecognized(IExtractorInputReader reader, MarkerSetHolder markerSetHolder) {
		MarkerSet markerSet = markerSetHolder
				.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		putLabelsRecognized(reader, markerSet);
		markerSet = markerSetHolder
					.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		putLabelsRecognized(reader, markerSet);
		
		return markerSetHolder;
	}
	
	
	
	private void putLabelsRecognized(IExtractorInputReader reader, MarkerSet markerSet) {
		if(markerSet == null){
			return;
		}
		for (Marker marker : markerSet.getMarkers()) {
			Map<String, IValues> fvv = getExtractorReaderService()
					.findAllVectorValuesForMarker(reader, marker);
			RecognitionResult result = getMatchingService().match(markerSet.getMarkerSetType(),fvv);
			if (result != null) {
				marker.setLabel(result.getInfo().getName());
			}
		}
		
	}

	public WorkExtractorReaderService getExtractorReaderService() {
		if (extractorReaderService == null) {
			extractorReaderService = WorkServiceFactory
					.createExtractorReaderService();
		}
		return extractorReaderService;
	}

	public void setExtractorReaderService(
			WorkExtractorReaderService extractorReaderService) {
		this.extractorReaderService = extractorReaderService;
	}
	
	public MatchingServiceImpl getMatchingService() {
		if (matchingService == null) {
			matchingService = MatchingServiceImpl.getInstance();
		}
		return matchingService;
	}

	public void setMatchingService(MatchingServiceImpl matchingService) {
		this.matchingService = matchingService;
	}

	
}
