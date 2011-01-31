package org.spantus.work.ui.services.impl;

import java.util.Map;

import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.services.MarkerLabeling;

public class MarkerLabelingRecognitionImpl implements MarkerLabeling{

	private ExtractorReaderService extractorReaderService;
	private MatchingServiceImpl matchingService;
	
	
	public MarkerSetHolder label(MarkerSetHolder markerSetHolder, SpantusWorkInfo ctx, IExtractorInputReader reader) {
		getMatchingService().update(ctx.getProject().getRecognitionConfig());
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
		if (markerSet == null) {
			markerSet = markerSetHolder
					.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		}
		for (Marker marker : markerSet.getMarkers()) {
			Map<String, IValues> fvv = getExtractorReaderService()
					.findAllVectorValuesForMarker(reader, marker);
			RecognitionResult result = getMatchingService().match(fvv);
			if (result != null) {
				marker.setLabel(result.getInfo().getName());
			}
		}
		return markerSetHolder;
	}
	
	public ExtractorReaderService getExtractorReaderService() {
		if (extractorReaderService == null) {
			extractorReaderService = WorkServiceFactory
					.createExtractorReaderService();
		}
		return extractorReaderService;
	}

	public void setExtractorReaderService(
			ExtractorReaderService extractorReaderService) {
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
