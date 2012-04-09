package org.spantus.externals.recognition.segment;

import java.util.Map;

import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.services.CorpusService;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.extractor.ExtractorInputReader;
import org.spantus.logger.Logger;
import org.spantus.segment.online.DecisionSegmentatorOnline;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;

public class RecognitionSegmentatorOnline extends DecisionSegmentatorOnline {
	
	private ExtractorInputReader bufferedReader;

	
	private WorkExtractorReaderService extractorReaderService;

	
	private CorpusService corpusService;
	
	private Logger log = Logger.getLogger(getClass());
	
	private Boolean learnMode=
		Boolean.FALSE;
//		Boolean.TRUE;
	
	
	
	@Override
	protected boolean onSegmentEnded(Marker marker) {
		if(!super.onSegmentEnded(marker)) return false; 

                Map<String, IValues> featureData = getExtractorReaderService()
                        .findAllVectorValuesForMarker(bufferedReader, marker);

                if (getLearnMode()) {
                    getCorpusService().learn(marker.getLabel(), featureData);
                } else {
                    RecognitionResult result = getCorpusService().match(featureData);
                    if (result == null) {
                        log.error("Does not matched");
                    }
                    marker.setLabel(getCorpusService().match(featureData).getInfo().getName());
                }
		
		log.error(marker.toString());
		return true;
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
        public WorkExtractorReaderService getExtractorReaderService() {
            if(extractorReaderService==null){
                 this.extractorReaderService =  WorkServiceFactory.createExtractorReaderService();
            }
            return extractorReaderService;
        }
}
