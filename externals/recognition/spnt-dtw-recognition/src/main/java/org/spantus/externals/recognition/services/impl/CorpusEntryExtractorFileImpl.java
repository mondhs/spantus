/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.externals.recognition.services.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.core.wav.AudioManager;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.exception.ProcessingException;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.services.CorpusEntryExtractor;
import org.spantus.externals.recognition.services.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.Assert;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.ExtractorReaderServiceImpl;

/**
 *
 * @author mondhs
 */
public class CorpusEntryExtractorFileImpl implements CorpusEntryExtractor {

    private static final Logger log = Logger.getLogger(CorpusEntryExtractorFileImpl.class);
    private ExtractorReaderService readerService;
    private ExtractorEnum[] extractors;
    private ISegmentatorService segmentator;
    private CorpusService corpusService;
    private AudioManager audioManager;
    private OnlineDecisionSegmentatorParam segmentionParam;
    private int windowLengthInMilSec = ExtractorsFactory.DEFAULT_WINDOW_LENGHT;
    private int overlapInPerc =  ExtractorsFactory.DEFAULT_WINDOW_OVERLAP;

    /**
     * Find segments(markers), then put them to corpus
     * @param filePath
     * @return
     */
    public List<CorpusEntry> extractInMemory(File filePath) {
        Assert.isTrue(filePath.exists(), "file not exists" + filePath);
        List<CorpusEntry> result = new ArrayList<CorpusEntry>();
        //find markers
        IExtractorInputReader reader = getReaderService().createReaderWithClassifier(
                getExtractors(), filePath);

        MarkerSetHolder markerSetHorlder = findMarkers(reader);
        MarkerSet segments = getSegementedMarkers(markerSetHorlder);

        
        //process markers
        Assert.isTrue(segments != null);
        for (Marker marker : segments.getMarkers()) {
            CorpusEntry corpusEntry = create( marker, reader);
            result.add(corpusEntry);
        }

        return result;
    }

    /**
     * 
     * @param filePath
     * @return
     */
    public MarkerSetHolder extractAndLearn(File filePath) {
         Assert.isTrue(filePath.exists(), "file not exists" + filePath);
        
        
        //find markers
        IExtractorInputReader reader = getReaderService().createReaderWithClassifier(
                getExtractors(), filePath);
        MarkerSetHolder markerSetHorlder = findMarkers(reader);

        MarkerSet segments = getSegementedMarkers(markerSetHorlder);
        log.debug("[extractAndLearn] marker size {0}", segments.getMarkers().size());
        
        extractAndLearn(filePath, markerSetHorlder, reader);

        return markerSetHorlder;
    }
    
     public MarkerSetHolder extractAndLearn(File filePath, MarkerSetHolder markerSetHolder, IExtractorInputReader reader) {
        IExtractorInputReader localReader = null;
        if(reader == null){
            localReader = getReaderService().createReaderWithClassifier(
                getExtractors(), filePath);
        }else{
            localReader = reader;
        }
        MarkerSet segments = getSegementedMarkers(markerSetHolder);
        URL fileUrl = toUrl(filePath);
        int result = 0;
         //process markers
        Assert.isTrue(segments != null);
        for (Marker marker : segments.getMarkers()) {
            marker.setLabel(
            		MessageFormat.format("{0}-{1}-{2}", marker.getLabel().trim(), 
                    filePath.getName(), (result+1)).toString()
                    );
            
            if(marker.getLength()>10){
                    learn(fileUrl, marker, localReader);
            }
            result++;
        }
        return markerSetHolder;

     }
    
    /**
     * 
     * @param markerSetHolder
     * @return
     */
    protected MarkerSet getSegementedMarkers(MarkerSetHolder markerSetHolder){
       MarkerSet segments = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
        if(segments == null){
            segments = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
        }
       return segments;
    }
    
    /**
     * Find markers
     * @param filePath
     * @return
     */
    protected  MarkerSetHolder findMarkers(IExtractorInputReader reader) {
    	Collection<IClassifier> clasifiers = new ArrayList<IClassifier>();
    	for (IGeneralExtractor extractor : reader.getGeneralExtractor()) {
    		if(extractor instanceof IClassifier){
    			clasifiers.add((IClassifier) extractor);
    		}
		}
    	
        
        log.debug("[findMarkers] clasifiers size {0}", clasifiers.size());
        MarkerSetHolder markerSetHorlder = getSegmentator().extractSegments(
                clasifiers, getSegmentionParam());
        
        return markerSetHorlder;
    }


    /**
     * 
     * @param file
     * @return
     */
    protected URL toUrl(File file) {
        URL fileUrl = null;
        try {
            fileUrl = file.toURI().toURL();
            return fileUrl;
        } catch (MalformedURLException ex) {
            throw new ProcessingException(ex);
        }
    }

    protected CorpusEntry create(Marker marker, IExtractorInputReader reader) {
          Map<String, IValues> fvv = getReaderService().findAllVectorValuesForMarker(
                reader,
                marker);
        CorpusEntry corpusEntry = getCorpusService().create(marker.getLabel(), fvv);
        return corpusEntry;
    }
    /**
     * 
     * @param fileUrl
     * @param marker
     * @param reader
     * @return
     */
    public CorpusEntry learn(URL fileUrl, Marker marker, IExtractorInputReader reader) {

        AudioInputStream ais =
                AudioManagerFactory.createAudioManager().findInputStreamInMils(
                fileUrl,
                marker.getStart(),
                marker.getLength());

        CorpusEntry corpusEntry = create(marker, reader);
        getCorpusService().learn(corpusEntry, ais);
        return corpusEntry;
    }
    
    public OnlineDecisionSegmentatorParam getSegmentionParam() {
        if(segmentionParam == null){
            segmentionParam = new OnlineDecisionSegmentatorParam();
            segmentionParam.setMinLength(91L);
            segmentionParam.setMinSpace(61L);
            segmentionParam.setExpandStart(60L);
            segmentionParam.setExpandEnd(60L);
        }
        return segmentionParam;
    }

    public void setSegmentionParam(OnlineDecisionSegmentatorParam segmentionParam) {
        this.segmentionParam = segmentionParam;
    }
    
    
    

    public ISegmentatorService getSegmentator() {
        if (segmentator == null) {
            segmentator = SegmentFactory.createSegmentator(
                    SegmentatorServiceEnum.basic.name());
        }
        return segmentator;
    }

    public void setSegmentator(ISegmentatorService segmentator) {
        this.segmentator = segmentator;
    }

    public ExtractorReaderService getReaderService() {
        if (readerService == null) {
        	ExtractorReaderServiceImpl readerServiceImpl = new ExtractorReaderServiceImpl();
        	readerServiceImpl.setWindowLengthInMilSec(getWindowLengthInMilSec());
        	readerServiceImpl.setOverlapInPerc(getOverlapInPerc());
        	readerService = readerServiceImpl;
        }
        return readerService;
    }

    public void setReaderService(ExtractorReaderService readerService) {
        this.readerService = readerService;
    }

    public ExtractorEnum[] getExtractors() {
        if (extractors == null) {
            extractors = new ExtractorEnum[]{
                        ExtractorEnum.MFCC_EXTRACTOR,
                        ExtractorEnum.LPC_EXTRACTOR,
                        ExtractorEnum.FFT_EXTRACTOR,
                        ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
//                        ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR
                    };
        }
        return extractors;
    }

    public void setExtractors(ExtractorEnum[] extractors) {
        this.extractors = extractors;
    }

    public CorpusService getCorpusService() {
        if (corpusService == null) {
            corpusService = RecognitionServiceFactory.createCorpusService();
        }
        return corpusService;
    }

    public void setCorpusService(CorpusService corpusService) {
        this.corpusService = corpusService;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

	public int getWindowLengthInMilSec() {
		return windowLengthInMilSec;
	}

	public void setWindowLengthInMilSec(int windowLengthInMilSec) {
		this.windowLengthInMilSec = windowLengthInMilSec;
		setReaderService(null);
	}

	public int getOverlapInPerc() {
		return overlapInPerc;
	}

	public void setOverlapInPerc(int overlapInPerc) {
		this.overlapInPerc = overlapInPerc;
		setReaderService(null);
	}
}
