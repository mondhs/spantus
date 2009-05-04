package org.spnt.recognition.segment;

import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.marker.Marker;
import org.spantus.extractor.ExtractorInputReader;
import org.spantus.logger.Logger;
import org.spantus.segment.online.DecistionSegmentatorOnline;
import org.spantus.work.services.FeatureExtractor;
import org.spantus.work.services.FeatureExtractorImpl;
import org.spnt.recognition.services.CorpusService;
import org.spnt.recognition.services.CorpusServiceBaseImpl;

public class RecognitionSegmentatorOnline extends DecistionSegmentatorOnline {
	
	private ExtractorInputReader bufferedReader;
	
	private FeatureExtractor featureExtractor;
	
	private CorpusService corpusService;
	
	private Logger log = Logger.getLogger(getClass());
	
	private Boolean learnMode=
		Boolean.FALSE;
//		Boolean.TRUE;
	
	
	
	@Override
	protected boolean onSegmentEnded(Marker marker) {
		if(!super.onSegmentEnded(marker)) return false; 
		FrameVectorValues values = 
			((IExtractorVector)getFeatureExtractor().findExtractorByName("LPC", bufferedReader))
			.getOutputValues();
		Float fromIndex = (marker.getStart().floatValue()*values.getSampleRate())/1000;
		Float toIndex = fromIndex+(marker.getLength().floatValue()*values.getSampleRate())/1000;
		FrameVectorValues fvv = values.subList(fromIndex.intValue(), toIndex.intValue());
		if(getLearnMode()){
			getCorpusService().learn(marker.getLabel(),fvv);
		}else{
			marker.setLabel(getCorpusService().match(fvv).getInfo().getName());
		}
		log.error(marker.toString());
		return true;
	}

	public FeatureExtractor getFeatureExtractor() {
		if (featureExtractor == null) {
			featureExtractor = new FeatureExtractorImpl();
		}
		return featureExtractor;
	}
	
	public IExtractorInputReader getBufferedReader() {
		return bufferedReader;
	}
	public CorpusService getCorpusService() {
		if (corpusService == null) {
			corpusService = new CorpusServiceBaseImpl();
		}
		return corpusService;
	}
	public Boolean getLearnMode() {
		return learnMode;
	}
	public RecognitionSegmentatorOnline(ExtractorInputReader bufferedReader){
		this.bufferedReader = bufferedReader;
	}
}
