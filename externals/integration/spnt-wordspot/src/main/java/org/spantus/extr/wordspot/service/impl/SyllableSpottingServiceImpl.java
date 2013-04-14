package org.spantus.extr.wordspot.service.impl;

import java.net.URL;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfigAware;

import org.spantus.extr.wordspot.service.SegmentExtractorService;
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.segment.online.AsyncMarkerSegmentatorListenerImpl;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.3
 * Created: May 7, 2012
 *
 */
public class SyllableSpottingServiceImpl  implements SegmentExtractorServiceConfigAware, SpottingService{
	
	private SegmentExtractorService segmentExtractorService;
	private String syllableRepositoryPath;
        private SegmentExtractorServiceConfig serviceConfig;
		private int operationCount;
	
	public SyllableSpottingServiceImpl(String syllableRepositoryPath) {
		this.syllableRepositoryPath = syllableRepositoryPath;
	}

    @Override
	public void wordSpotting(URL urlFile, SpottingListener wordSpottingListener){
    	SpottingMarkerSegmentatorListenerImpl listener = new SpottingMarkerSegmentatorListenerImpl(wordSpottingListener);
                listener.setServiceConfig(serviceConfig);
		listener.setRepositoryPath(syllableRepositoryPath);
		AsyncMarkerSegmentatorListenerImpl asyncLister = new AsyncMarkerSegmentatorListenerImpl(listener);
		getSegmentExtractorService().listenSegments(urlFile, asyncLister);
		this.operationCount = listener.getOperationCount();
	}

	public SegmentExtractorService getSegmentExtractorService() {
		if(segmentExtractorService == null){
			SegmentExtractorServiceImpl aSegmentExtractorService = new SegmentExtractorServiceImpl();
                        aSegmentExtractorService.setServiceConfig(serviceConfig);
                        segmentExtractorService=aSegmentExtractorService;
		}
		return segmentExtractorService;
	}

	public void setSegmentExtractorService(
			SegmentExtractorService segmentExtractorService) {
		this.segmentExtractorService = segmentExtractorService;
	}

    @Override
    public void setServiceConfig(SegmentExtractorServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

	public int getOperationCount() {
		return this.operationCount;
	}
}
