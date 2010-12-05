/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.externals.recognition.services.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sound.sampled.AudioInputStream;
import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.exception.ProcessingException;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.services.CorpusEntryExtractor;
import org.spantus.externals.recognition.services.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;
import org.spantus.segment.SegmentatorParam;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.Assert;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.ExtractorReaderServiceImpl;
import org.spantus.core.wav.AudioManager;
import org.spantus.core.wav.AudioManagerFactory;

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

    /**
     * Find segments(markers), then put them to corpus
     * @param filePath
     * @return
     */
    public List<CorpusEntry> extractInMemory(File filePath) {
        Assert.isTrue(filePath.exists(), "file not exists" + filePath);
        URL fileUrl = toUrl(filePath);
        List<CorpusEntry> result = new ArrayList<CorpusEntry>();
        //find markers
        IExtractorInputReader reader = getReaderService().createReaderWithClassifier(
                getExtractors(), filePath);

        MarkerSet words = findMarkers(reader);

        //process markers
        Assert.isTrue(words != null);
        for (Marker marker : words.getMarkers()) {
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
    public int extractAndLearn(File filePath) {
         Assert.isTrue(filePath.exists(), "file not exists" + filePath);
        URL fileUrl = toUrl(filePath);
        int result = 0;
        //find markers
        IExtractorInputReader reader = getReaderService().createReaderWithClassifier(
                getExtractors(), filePath);
        MarkerSet words = findMarkers(reader);

        //process markers
        Assert.isTrue(words != null);
        for (Marker marker : words.getMarkers()) {
            marker.setLabel(filePath.getName()+"-"+
                    (result+1));
            learn(fileUrl, marker, reader);
            result++;
        }

        return result;
    }

    /**
     * Find markers
     * @param filePath
     * @return
     */
    protected MarkerSet findMarkers(IExtractorInputReader reader) {

        Set<IClassifier> clasifiers = new HashSet<IClassifier>();
        for (IGeneralExtractor extractor : reader.getGeneralExtractor()) {
            if (extractor instanceof IClassifier) {
                clasifiers.add((IClassifier) extractor);
            }
        }
        log.debug("[findMarkers] clasifiers size {0}", clasifiers.size());
        MarkerSetHolder markerSetHorlder = getSegmentator().extractSegments(
                clasifiers, getSegmentionParam());
        MarkerSet words = markerSetHorlder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
        log.debug("[findMarkers] marker size {0}", words.getMarkers().size());
        return words;
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
    protected CorpusEntry learn(URL fileUrl, Marker marker, IExtractorInputReader reader) {

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
                    SegmentatorServiceEnum.online.name());
        }
        return segmentator;
    }

    public void setSegmentator(ISegmentatorService segmentator) {
        this.segmentator = segmentator;
    }

    public ExtractorReaderService getReaderService() {
        if (readerService == null) {
            readerService = new ExtractorReaderServiceImpl();
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
}
