package org.spantus.extr.wordspot.service.impl;

import java.net.URL;

import org.spantus.extr.wordspot.service.SegmentExtractorService;
import org.spantus.extr.wordspot.service.WordSpottingListener;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.3
 * Created: May 7, 2012
 *
 */
public class WordSpottingServiceImpl {
	
	SegmentExtractorService segmentExtractorService;

	public void wordSpotting(URL urlFile, WordSpottingListener wordSpottingListener){
		SpottingMarkerSegmentatorListenerImpl listener = new SpottingMarkerSegmentatorListenerImpl(wordSpottingListener);
		getSegmentExtractorService().listenSegments(urlFile, listener);
		
	}

	public SegmentExtractorService getSegmentExtractorService() {
		if(segmentExtractorService == null){
			segmentExtractorService = new SegmentExtractorServiceImpl();
		}
		return segmentExtractorService;
	}

	public void setSegmentExtractorService(
			SegmentExtractorService segmentExtractorService) {
		this.segmentExtractorService = segmentExtractorService;
	}
}
