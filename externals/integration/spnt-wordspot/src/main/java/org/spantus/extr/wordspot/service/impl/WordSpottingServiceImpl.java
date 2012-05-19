package org.spantus.extr.wordspot.service.impl;

import java.net.URL;

import org.spantus.extr.wordspot.service.SegmentExtractorService;
import org.spantus.extr.wordspot.service.WordSpottingListener;
import org.spantus.segment.online.AsyncMarkerSegmentatorListenerImpl;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.3
 * Created: May 7, 2012
 *
 */
public class WordSpottingServiceImpl {
	
	private SegmentExtractorService segmentExtractorService;
	private String repositoryPath;
	
	public WordSpottingServiceImpl(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public void wordSpotting(URL urlFile, WordSpottingListener wordSpottingListener){
		SpottingMarkerSegmentatorListenerImpl listener = new SpottingMarkerSegmentatorListenerImpl(wordSpottingListener);
		listener.setRepositoryPath(repositoryPath);
		AsyncMarkerSegmentatorListenerImpl asyncLister = new AsyncMarkerSegmentatorListenerImpl(listener);
		getSegmentExtractorService().listenSegments(urlFile, asyncLister);
		
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
