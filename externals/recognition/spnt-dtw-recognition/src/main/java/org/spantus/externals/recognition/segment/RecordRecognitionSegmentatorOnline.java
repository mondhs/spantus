package org.spantus.externals.recognition.segment;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.IValues;
import org.spantus.core.marker.Marker;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.CorpusFileEntry;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.corpus.CorpusMatchListener;
import org.spantus.externals.recognition.services.CorpusService;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.logger.Logger;
import org.spantus.segment.io.RecordSegmentatorOnline;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;

public class RecordRecognitionSegmentatorOnline extends RecordSegmentatorOnline{
	
	private CorpusService corpusService;
	

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
        public URL saveSegmentAccepted(Marker marker, AudioInputStream ais, File file) {
            if(marker == null){
                    super.saveSegmentAccepted(marker,ais, file);
            }
            URL url = findMatchAndSave(marker, ais);
            return url;
        }
	/**
	 * 
	 * @param marker
	 */
        protected URL findMatchAndSave(Marker marker, AudioInputStream ais) {
            Map<String, IValues> featureData = getExtractorReaderService().findAllVectorValuesForMarker(getReader().getReader(), marker);
            URL url = null;
            if (getLearnMode()) {
                
                CorpusEntry corpusEntry = getCorpusService().create(marker.getLabel(), featureData);
                getCorpusService().learn(corpusEntry, ais);
                if (corpusEntry instanceof CorpusFileEntry) {
                    try {
                        url = ((CorpusFileEntry) corpusEntry).getWavFile().toURI().toURL();
                    } catch (MalformedURLException ex) {
                        log.error(ex);
                    }
                }
                log.debug("[findMatchAndSave] {0}", url );
            } else {
                RecognitionResult result = getCorpusService().match(featureData);
                if (result != null) {
                    marker.setLabel(result.getInfo().getName());
                    notifyCorpusMatchListener(result, marker);
                } else {
                    log.info("[findBestMatach] there is no match");
                }
            }
         
            log.info("[findBestMatach]" + marker);
            return url;

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
