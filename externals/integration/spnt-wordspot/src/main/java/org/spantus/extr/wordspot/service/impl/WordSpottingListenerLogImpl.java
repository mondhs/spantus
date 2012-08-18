package org.spantus.extr.wordspot.service.impl;

import java.util.*;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorInputReaderAware;
import org.spantus.core.marker.Marker;
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extr.wordspot.service.WordSpottingListener;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;
import org.spantus.utils.StringUtils;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;

/**
 *
 * @author Mindaugas Greibus
 * @since 0.3 Created: May 7, 2012
 *
 */
public class WordSpottingListenerLogImpl implements WordSpottingListener,IExtractorInputReaderAware {

    private static final Logger LOG = Logger.getLogger(WordSpottingListenerLogImpl.class);
    private List<RecognitionResult> wordMatches = new ArrayList<RecognitionResult>();
    private Map<RecognitionResult, SignalSegment> wordSegments = new HashMap<RecognitionResult, SignalSegment>();
    private List<SignalSegment> signalSegmentsSyllable = new ArrayList<SignalSegment>();
    private CorpusService corpusServiceWord;
    private String repositoryPathWord;
    private String targetWord;
    private Set<String> acceptableSyllableSet;
    private WorkExtractorReaderService extractorReaderService;
    private IExtractorInputReader extractorInputReader;
    
    private ExtractorEnum recognitionFeatureName = ExtractorEnum.MFCC_EXTRACTOR;

    public WordSpottingListenerLogImpl(String target, String[] acceptableSyllable, String repositoryPathSyllable) {
        this.repositoryPathWord = repositoryPathSyllable;
        this.targetWord = target;
        Assert.isTrue(acceptableSyllable!=null, "acceptableSyllable cannot be null");
        acceptableSyllableSet = new HashSet<String>();
        for (String string : acceptableSyllable) {
            acceptableSyllableSet.add(string);
        }
    }
    /**
     * 
     * @param sourceId
     * @param newSyllable
     * @param recognitionResults
     * @return 
     */
    @Override
    public String foundSegment(String sourceId, SignalSegment newSyllable, List<RecognitionResult> recognitionResults) {

        RecognitionResult rtnRecognitionResult = null;

        int index = 0;
        for (RecognitionResult recognitionResult : recognitionResults) {
            //take first one as default
            if (rtnRecognitionResult == null) {
                rtnRecognitionResult = recognitionResult;
            }


            if (acceptableSyllableSet.contains(recognitionResult.getInfo().getName().toLowerCase())) {
                RecognitionResult wordMatch = matchWord(signalSegmentsSyllable, newSyllable);
                if (wordMatch != null) {
                    getWordMatches().add(wordMatch);
                    rtnRecognitionResult = wordMatch;
                    signalSegmentsSyllable.clear();
                    break;
                }
                signalSegmentsSyllable.add(newSyllable);
                break;
            }else{

                
                LOG.debug("[processEndedSegment] reject syllable {0}",recognitionResult.getInfo().getName());  
            }

            //check for first 1 matches
            if (++index > 0) {
                break;
            }
        }
        String rtnName = null;
        if (rtnRecognitionResult != null) {
            rtnName = rtnRecognitionResult.getInfo().getName();
        }


        LOG.debug("[processEndedSegment] spotted: {0} in time [{1}:{2}] ",
                rtnName, newSyllable.getMarker().getStart(),
                newSyllable.getMarker().getEnd());
        return rtnName;
    }
    /**
     * 
     * @param existingSyllableSegments
     * @param newSyllable
     * @return 
     */
    private RecognitionResult matchWord(List<SignalSegment> existingSyllableSegments, SignalSegment newSyllable) {
        if (existingSyllableSegments.isEmpty()) {
            return null;
        }
       
        SignalSegment segmentWord = newSignalSegmentWord(existingSyllableSegments, newSyllable);
        

        List<RecognitionResult> resultList = getCorpusServiceWord().findMultipleMatchFull(segmentWord);
        if(!isResultAcceptable(resultList)){
            resultList = Collections.emptyList();
        }
        RecognitionResult firstResult = null;
        for (RecognitionResult recognitionResult : resultList) {
//            	if(recognitionResult.getDetails().getDistances().get(getRecognitionFeatureName().name()).compareTo(10000D)>0){
//			break;
//		}
            String recognitionResultName = recognitionResult.getInfo().getName();
            if(firstResult == null){
                firstResult = recognitionResult;
                segmentWord.getMarker().setLabel(recognitionResultName);
            }
            LOG.debug("[matchWord] result: {0} [{1}]", recognitionResult.getInfo().getName(),
                    recognitionResult.getDetails().getDistances());
        }
        if(firstResult== null || "-".equals(firstResult.getInfo().getName())){
            firstResult = null;
        }
        if(firstResult!=null){
            getWordSegments().put(firstResult, segmentWord);
        }
        
        

        return firstResult;
    }
    /**
     * 
     * @param existingSyllableSegments
     * @param newSyllable
     * @return 
     */
    private SignalSegment newSignalSegmentWord(List<SignalSegment> existingSyllableSegments, SignalSegment newSyllable) {
        SignalSegment firstSyllable = existingSyllableSegments.get(0);
        SignalSegment segmentWord = new SignalSegment();
        LOG.debug("[matchWord] merge {0}[{2}] to {1}[{3}]",
                firstSyllable.getMarker(),
                newSyllable.getMarker(),
                firstSyllable.getMarker().getStart(),
                newSyllable.getMarker().getEnd());
        if (firstSyllable.getMarker().getStart() > newSyllable.getMarker().getEnd()) {
            LOG.debug("[matchWord] existing syllable +++++");
            for (SignalSegment signalSegment : existingSyllableSegments) {
                LOG.debug("[matchWord] existing syllable: {0}",
                        signalSegment.getMarker());
            }
            LOG.debug("[matchWord] existing syllable -----");
        }


        Assert.isTrue(firstSyllable.getMarker().getStart() < newSyllable.getMarker().getEnd(), "last syllable should start befor new is finished");
        

        segmentWord.setMarker(new Marker());
        segmentWord.getMarker().setStart(firstSyllable.getMarker().getStart());
        segmentWord.getMarker().setLength(0L);
        

        segmentWord.getMarker().setEnd(newSyllable.getMarker().getEnd());
        
        Map<String, IValues> fvv  = getExtractorReaderService().recalcualteValues(getExtractorInputReader(), 
                segmentWord.getMarker(), 
                getRecognitionFeatureName().name());
        IValues mffcValues = fvv.get(getRecognitionFeatureName().name());
        Assert.isTrue(mffcValues.size() > 0, "{1} is not calculated. Size {0} ", mffcValues.size(), getRecognitionFeatureName());
        segmentWord.getFeatureFrameVectorValuesMap().put(getRecognitionFeatureName().name(), 
                new FrameVectorValuesHolder((FrameVectorValues)mffcValues));
        

        
        return segmentWord;
    }


    public CorpusService getCorpusServiceWord() {
        if (corpusServiceWord == null) {
            Assert.isTrue(StringUtils.hasText(repositoryPathWord), "Repository path not set");
            corpusServiceWord = RecognitionServiceFactory.createCorpusServicePartialSearch(repositoryPathWord);
        }
        return corpusServiceWord;
    }

    public List<RecognitionResult> getWordMatches() {
        return wordMatches;
    }

    public Map<RecognitionResult, SignalSegment> getWordSegments() {
        return wordSegments;
    }

    public String getTarget() {
        return targetWord;
    }

    public void setTarget(String target) {
        this.targetWord = target;
    }
     public WorkExtractorReaderService getExtractorReaderService() {
        if (extractorReaderService == null) {
            extractorReaderService = WorkServiceFactory.createExtractorReaderService();
        }
        return extractorReaderService;
    }

    public void setExtractorReaderService(WorkExtractorReaderService extractorReaderService) {
        this.extractorReaderService = extractorReaderService;
    }

    @Override
    public void setExtractorInputReader(IExtractorInputReader reader) {
        this.extractorInputReader = reader;
    }

    public IExtractorInputReader getExtractorInputReader() {
        return extractorInputReader;
    }

    private boolean isResultAcceptable(List<RecognitionResult> resultList) {
        if(resultList == null || resultList.size()<2){
            return true;
        }
        RecognitionResult first = resultList.get(0);
        Double firstScore = first.getScores().get(getRecognitionFeatureName().name());
        RecognitionResult second = resultList.get(1);
        Double secondScore = second.getScores().get(getRecognitionFeatureName().name());
        boolean sameLabels = first.getInfo().getName().equals(second.getInfo().getName());
        double scoreDelta = secondScore - firstScore;
        if(!sameLabels){
            return scoreDelta > .1;
        }
        return true;
    }

    public ExtractorEnum getRecognitionFeatureName() {
        return recognitionFeatureName;
    }

    public void setRecognitionFeatureName(ExtractorEnum recognitionFeatureName) {
        this.recognitionFeatureName = recognitionFeatureName;
    }
    
    
}
