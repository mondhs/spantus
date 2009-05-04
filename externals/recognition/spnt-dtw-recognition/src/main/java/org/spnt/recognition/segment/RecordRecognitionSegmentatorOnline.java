package org.spnt.recognition.segment;

import java.net.URL;

import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.marker.Marker;
import org.spantus.logger.Logger;
import org.spantus.segment.io.RecordSegmentatorOnline;
import org.spantus.work.services.FeatureExtractor;
import org.spantus.work.services.FeatureExtractorImpl;
import org.spnt.recognition.bean.RecognitionResult;
import org.spnt.recognition.corpus.CorpusMatchListener;
import org.spnt.recognition.services.CorpusService;
import org.spnt.recognition.services.CorpusServiceBaseImpl;

public class RecordRecognitionSegmentatorOnline extends RecordSegmentatorOnline{
	
	private CorpusService corpusService;
	
	private FeatureExtractorImpl featureExtractor;
	
	private CorpusMatchListener corpusMatchListener ;
	
	private Boolean learnMode =
//		Boolean.FALSE;
		Boolean.TRUE;
	
	private Logger log = Logger.getLogger(getClass());

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
	protected void findBestMatach(Marker marker){
		FrameVectorValues values = 
			((IExtractorVector)getFeatureExtractor().findExtractorByName(
					"LPC", getReader().getReader()))
			.getOutputValues();
		Float fromIndex = (marker.getStart().floatValue()*values.getSampleRate())/1000;
		Float toIndex = fromIndex+(marker.getLength().floatValue()*values.getSampleRate())/1000;
		if(toIndex>=values.size()){
			//calculating offset for cleaned up buffer
			int bufferIndex = (toIndex.intValue()/values.size())-1;
			int offset = toIndex.intValue()%values.size() 
				+ (values.size() * bufferIndex);
			fromIndex-=offset;
			toIndex-=offset;
		}
		FrameVectorValues fvv = values.subList(fromIndex.intValue(), toIndex.intValue());
		if(getLearnMode()){
			getCorpusService().learn(marker.getLabel(), fvv);
		}else{
			//search for match
			notifyCorpusMatchListener(getCorpusService().match(fvv), marker);
		}
		log.error(marker.toString());
		
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

	
	public CorpusMatchListener getCorpusMatchListener() {
		return corpusMatchListener;
	}


	public void setCorpusMatchListener(CorpusMatchListener corpusMatchListener) {
		this.corpusMatchListener = corpusMatchListener;
	}
	
	

}
