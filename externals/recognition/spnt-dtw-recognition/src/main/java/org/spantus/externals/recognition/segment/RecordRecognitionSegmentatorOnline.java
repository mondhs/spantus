package org.spantus.externals.recognition.segment;

import java.net.URL;

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
import org.spantus.work.services.FeatureExtractor;
import org.spantus.work.services.FeatureExtractorImpl;

public class RecordRecognitionSegmentatorOnline extends RecordSegmentatorOnline{
	
	private CorpusService corpusService;
	
	private FeatureExtractorImpl featureExtractor;
	
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
	protected void findBestMatach(Marker marker){
		for (IExtractorVector extractor : getReader().getReader().getExtractorRegister3D()) {
			
			FrameVectorValues values = extractor.getOutputValues();
			Float fromIndex = (marker.getStart().floatValue()*values.getSampleRate())/1000;
			Float toIndex = fromIndex+(marker.getLength().floatValue()*values.getSampleRate())/1000;
			FrameVectorValues fvv = values.subList(fromIndex.intValue(), toIndex.intValue());
			
			FeatureData featureData = new FeatureData();
			featureData.setName(extractor.getName());
			featureData.setValues(fvv);
			
			if(getLearnMode()){
				getCorpusService().learn(marker.getLabel(),featureData);
			}else{
				marker.setLabel(getCorpusService().match(featureData).getInfo().getName());
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
	
	
	

}
