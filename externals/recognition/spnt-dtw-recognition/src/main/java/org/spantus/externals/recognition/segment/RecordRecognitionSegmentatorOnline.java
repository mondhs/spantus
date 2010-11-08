package org.spantus.externals.recognition.segment;

import java.net.URL;
import java.util.Map;

import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.marker.Marker;
import org.spantus.externals.recognition.bean.FeatureData;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.corpus.CorpusMatchListener;
import org.spantus.externals.recognition.services.CorpusService;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.logger.Logger;
import org.spantus.segment.io.RecordSegmentatorOnline;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.FeatureExtractor;
import org.spantus.work.services.FeatureExtractorImpl;
import org.spantus.work.services.WorkServiceFactory;

public class RecordRecognitionSegmentatorOnline extends RecordSegmentatorOnline{
	
	private CorpusService corpusService;
	
	private FeatureExtractorImpl featureExtractor;

        private ExtractorReaderService extractorReaderService;
	
	private CorpusMatchListener corpusMatchListener ;
	
	private Boolean learnMode =
		Boolean.FALSE;
//		Boolean.TRUE;
	
	private Logger log = Logger.getLogger(RecordRecognitionSegmentatorOnline.class);

	/**
	 * 
	 */
	@Override
	public URL processAcceptedSegment(Marker marker){
		URL path = 
//			null;
			super.processAcceptedSegment(marker);
		findBestMatach(marker);
		return path;
	}
	/**
	 * 
	 * @param marker
	 */
        protected void findBestMatach(Marker marker) {
            Map<String, FrameVectorValues> featureData = getExtractorReaderService().findAllVectorValuesForMarker(getReader().getReader(), marker);
            if (getLearnMode()) {
                getCorpusService().learn(marker.getLabel(), featureData);
            } else {
                RecognitionResult result = getCorpusService().match(featureData);
                if (result != null) {
                    marker.setLabel(result.getInfo().getName());
                } else {
                    log.info("[findBestMatach] there is no match");
                }
            }

            log.info("[findBestMatach]" + marker);

        }

	/**
	 * 
	 * @param result
	 * @param marker
	 */
	protected void notifyCorpusMatchListener(RecognitionResult result, Marker marker){
		if(result != null){
			marker.setLabel(result.getInfo().getName());	
		}
		if(getCorpusMatchListener() != null){
			getCorpusMatchListener().matched(result);
		}
	}

	public CorpusService getCorpusService() {
		if (corpusService == null) {
			corpusService = new CorpusServiceBaseImpl();
		}
		return corpusService;
	}
	public FeatureExtractor getFeatureExtractor() {
		if (featureExtractor == null) {
			featureExtractor = new FeatureExtractorImpl();
		}
		return featureExtractor;
	}
	
	public Boolean getLearnMode() {
		return learnMode;
	}
	public void setLearnMode(Boolean learnMode) {
		this.learnMode = learnMode;
	}
	
	public CorpusMatchListener getCorpusMatchListener() {
		return corpusMatchListener;
	}


	public void setCorpusMatchListener(CorpusMatchListener corpusMatchListener) {
		this.corpusMatchListener = corpusMatchListener;
	}
	
        public ExtractorReaderService getExtractorReaderService() {
            if (extractorReaderService == null) {
                this.extractorReaderService = WorkServiceFactory.createExtractorReaderService();
            }
            return extractorReaderService;
        }

	

}
