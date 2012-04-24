package org.spantus.android.service;

import org.spantus.core.marker.Marker;
import org.spantus.core.service.ExtractorInputReaderService;
import org.spantus.core.service.impl.ExtractorInputReaderServiceImpl;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.segment.online.DecisionSegmentatorOnline;
import org.spantus.segment.online.rule.DecisionCtx;

/**
 * 
 * @author mgreibus
 * @since 0.3
 * 
 * 
 */
public class ExtractMarkerOnlineSegmentatorListener extends DecisionSegmentatorOnline {
	
//	private static final Logger LOG = Logger.getLogger(ExtractMarkerOnlineSegmentatorListener.class);

//	private ExtractorInputReader bufferedReader;

	private ExtractorInputReaderService extractorInputReaderService;

//	public ExtractMarkerOnlineSegmentatorListener(ExtractorInputReader bufferedReader) {
//		this.bufferedReader = bufferedReader;
//	}
	
	@Override
	public void onProcessNoise(DecisionCtx ctx, SegmentEvent event) {
		super.onProcessNoise(ctx, event);
//		LOG.debug("N: {0}=>{1}",event.getSample(), event.getValue());
	}
	
	@Override
	public void onProcessSegment(DecisionCtx ctx, SegmentEvent event) {
		super.onProcessSegment(ctx, event);
//		LOG.debug("S: {0}=>{1}",event.getSample(), event.getValue());
	}

	
	@Override
	protected boolean onSegmentEnded(Marker marker) {
		if (!super.onSegmentEnded(marker))
			return false;

//		LOG.debug("Ended: {0}", marker);
//		Map<String, IValues> featureData = getExtractorInputReaderService()
//				.findAllVectorValuesForMarker(bufferedReader, marker);

		return true;
	}
//
//	public IExtractorInputReader getBufferedReader() {
//		return bufferedReader;
//	}

	public ExtractorInputReaderService getExtractorInputReaderService() {
		if (extractorInputReaderService == null) {
			extractorInputReaderService = new ExtractorInputReaderServiceImpl();
		}
		return extractorInputReaderService;
	}

	public void setExtractorInputReaderService(
			ExtractorInputReaderService extractorInputReaderService) {
		this.extractorInputReaderService = extractorInputReaderService;
	}

}
