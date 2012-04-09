package org.spantus.segment;

import java.util.ArrayList;
import java.util.Collection;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.logger.Logger;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;

public class SegmentationServiceImpl {
	private static final Logger LOG = Logger
			.getLogger(SegmentationServiceImpl.class);

	private ISegmentatorService segmentator;
	private OnlineDecisionSegmentatorParam segmentionParam;
	private String segmentatorServiceType = SegmentatorServiceEnum.offline
			.name();

	/**
	 * Find markers
	 * 
	 * @param filePath
	 * @return
	 */
	public MarkerSetHolder findMarkers(IExtractorInputReader reader) {
		Collection<IClassifier> clasifiers = new ArrayList<IClassifier>();
		for (IGeneralExtractor extractor : reader.getGeneralExtractor()) {
			if (extractor instanceof IClassifier) {
				clasifiers.add((IClassifier) extractor);
			}
		}

		LOG.debug("[findMarkers] clasifiers size {0}", clasifiers.size());
		MarkerSetHolder markerSetHorlder = getSegmentator().extractSegments(
				clasifiers, getSegmentionParam());

		return markerSetHorlder;
	}
	
	/**
	 * 
	 * @param markerSetHolder
	 * @return
	 */
	public MarkerSet findSegementedLowestMarkers(MarkerSetHolder markerSetHolder) {
		MarkerSet segments = markerSetHolder.getMarkerSets().get(
				MarkerSetHolderEnum.phone.name());
		if (segments == null) {
			segments = markerSetHolder.getMarkerSets().get(
					MarkerSetHolderEnum.word.name());
		}
		return segments;
	}
	
	
	
	

	public ISegmentatorService getSegmentator() {
		if (segmentator == null) {
			segmentator = SegmentFactory
					.createSegmentator(getSegmentatorServiceType());
		}
		return segmentator;
	}

	public void setSegmentator(ISegmentatorService segmentator) {
		this.segmentator = segmentator;
	}

	public String getSegmentatorServiceType() {
		return segmentatorServiceType;
	}

	public void setSegmentatorServiceType(String segmentatorServiceType) {
		this.segmentatorServiceType = segmentatorServiceType;
	}
	public OnlineDecisionSegmentatorParam getSegmentionParam() {
		if (segmentionParam == null) {
			segmentionParam = new OnlineDecisionSegmentatorParam();
			segmentionParam.setMinLength(91L);
			segmentionParam.setMinSpace(61L);
			segmentionParam.setExpandStart(60L);
			segmentionParam.setExpandEnd(60L);
		}
		return segmentionParam;
	}

	public void setSegmentionParam(OnlineDecisionSegmentatorParam segmentionParam) {
		this.segmentionParam = segmentionParam;
	}
}
