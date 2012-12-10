/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.scrolling.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.service.CorpusService;
import org.spantus.extr.wordspot.dto.SpottingSyllableCtx;
import org.spantus.extr.wordspot.service.SegmentRecognitionThresholdService;
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.extr.wordspot.service.impl.SpottingService;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.utils.Assert;
import org.spantus.work.services.WorkExtractorReaderService;

/**
 *
 * @author mondhs
 * @since 0.3
 */
public class WindowScrollingSpottingServiceImpl implements SpottingService {

	private static final Logger LOG = LoggerFactory.getLogger(WindowScrollingSpottingServiceImpl.class);
    private WorkExtractorReaderService extractorReaderService;
    private CorpusService corpusService;
    private List<SignalSegment> keySegmentList;
    private long delta = 10;
	private SegmentRecognitionThresholdService segmentRecognitionThresholdService;
    /**
     * 
     * @param urlFile
     * @param wordSpottingListener 
     */
    @Override
    public void wordSpotting(URL urlFile, SpottingListener spottingListener) {
        Assert.isTrue(getKeySegmentList() != null || getKeySegmentList().isEmpty(), "keyword cannot be null, please, setKeySegmentList(...) first");
        Map<SignalSegment, SpottingSyllableCtx> ctxMap = newSpottingSyllableCtx(getKeySegmentList() );
        IExtractorInputReader aReader = createReader(urlFile);
        long availableStartMs = calcAvailableStartMs(getKeySegmentList(), aReader.getAvailableSignalLengthMs());
        
        LOG.debug("[wordSpotting] delta {}", getDelta());
        
        for (long start = getDelta(); start < availableStartMs; start += getDelta()) {
            for (SignalSegment keySegment : getKeySegmentList()) {
            	Marker iMarker = new Marker(start,
                		keySegment.getMarker().getLength(),""	);
                SignalSegment segment = recalculateFeatures(aReader, iMarker);
                if (segment == null) {
                    continue;
                }
                List<RecognitionResult> result = match(segment);
                SpottingSyllableCtx ctx = ctxMap.get(keySegment);
                SignalSegment foundSignalSegment = processAndContinue(keySegment, result, ctxMap.get(keySegment), start);
                if(foundSignalSegment !=null){
                    LOG.debug("[wordSpotting] foundSignalSegment {}", foundSignalSegment);
                	spottingListener.foundSegment(null, foundSignalSegment, ctx.getResultMap().get(foundSignalSegment.getMarker().getStart()));
                }
			}
        }
//        ctxMap.get(getKeySegmentList().get(0)).printMFCC();
//        ctxMap.get(getKeySegmentList().get(1)).printMFCC();
        ctxMap.size();

    }
    
    private long calcAvailableStartMs(List<SignalSegment> aKeySegmentList, long availableSignalLengthMs) {
    	long maxLength = - Long.MAX_VALUE;
    	for (SignalSegment signalSegment : aKeySegmentList) {
    		maxLength = Math.max(maxLength, signalSegment.getMarker().getLength());
		}
    	long availableStartMs = availableSignalLengthMs - 
    	maxLength;
		return availableStartMs;
	}

	private Map<SignalSegment, SpottingSyllableCtx> newSpottingSyllableCtx(List<SignalSegment> list) {
    	Map<SignalSegment, SpottingSyllableCtx> ctxMap = new HashMap<SignalSegment, SpottingSyllableCtx>();
    	for (SignalSegment signalSegment : list) {
    		SpottingSyllableCtx ctx = new SpottingSyllableCtx();
            ctx.setMinFirstMfccValue(null);
    		ctxMap.put(signalSegment, ctx);
		}
    	
		return ctxMap;
	}
	/**
     * Friendly method for testing
     * @param urlFile
     * @return 
     */
    public IExtractorInputReader createReader(URL urlFile) {
       return getExtractorReaderService().createReader(new ExtractorEnum[]{ExtractorEnum.SIGNAL_EXTRACTOR}, urlFile);
    }
    
        private SignalSegment processAndContinue(SignalSegment keySegment, List<RecognitionResult> result, 
                SpottingSyllableCtx ctx,
                Long start) {
        	
        	String keyLabel = keySegment.getMarker().getLabel();
            if (result.isEmpty()) {
                return null;
            }
            RecognitionResult firstMatch = null;
            RecognitionResult firstGoodMatch = null;
            for (RecognitionResult recognitionResult : result) {
                if(firstMatch == null){
                    firstMatch = recognitionResult;
                }
                if (keyLabel.equals(
                        recognitionResult.getInfo().getName())) {
                    firstGoodMatch = recognitionResult;
                    break;
                }
            }
            if (firstGoodMatch == null) {
                return null;
            }
            String name = firstGoodMatch.getInfo().getName();
//            firstMatch.getInfo().getName();
            Double firstMfccValue = firstGoodMatch.getDetails().getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name());
            //firstMatch.getDetails().getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name());
            
            ctx.getResultMap().put(start, result);
            ctx.getSyllableNameMap().put(start, name);
            ctx.getMinMfccMap().put(start, firstMfccValue);
            
            
            if(segmentRecognitionThresholdService.checkIfBellowThreshold(keyLabel, firstGoodMatch)){
                Double firstGoodMatchMfccValue = firstGoodMatch.getDetails().getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name());
                if(ctx.getMinFirstMfccValue() == null){
                    //if search minimum was not started                	
                    ctx.setMinFirstMfccValue(firstGoodMatchMfccValue);
                    ctx.setMinFirstMfccStart(start);
                    LOG.debug("started search value {}", start);
                }else{
                	//if we are under threshold for some time              
                    if(ctx.getMinFirstMfccValue().doubleValue()>firstGoodMatchMfccValue.doubleValue()){
                        ctx.setMinFirstMfccValue(firstGoodMatchMfccValue);
                        ctx.setMinFirstMfccStart(start);
                        LOG.debug("keep searching value min {}", start);
                    }else{
                    	LOG.debug("keep searching value to big {}", start);
                    }
                }
            	return null;
            }else{
            	if(ctx.getMinFirstMfccValue() != null){
                    SignalSegment signalSegment = new SignalSegment(new Marker(
                            ctx.getMinFirstMfccStart(), 
                            keySegment.getMarker().getLength(),
                            keyLabel));
                    ctx.setMinFirstMfccValue(null);
                    ctx.setMinFirstMfccStart(null);
                    LOG.debug("found min value {}", signalSegment);
                    return signalSegment;
            	}
            }
            

            
            return null;
        }

    
    /**
     * 
     * 
     * @param segment
     * @return 
     */
    public List<RecognitionResult> match(SignalSegment segment) {
        List<RecognitionResult> result = getCorpusService().findMultipleMatchFull(segment);

        if (result == null) {
            throw new IllegalArgumentException("No recognition information in corpus is found");
        }

        return result;
    }
    /**
     * Friendly method for testing
     * 
     * @param theExtractorReader
     * @param theMarker
     * @return 
     */
    public SignalSegment recalculateFeatures(IExtractorInputReader theExtractorReader, Marker theMarker) {
        Map<String, IValues> fvv = getExtractorReaderService().recalcualteValues(theExtractorReader,
                theMarker,
                ExtractorEnum.MFCC_EXTRACTOR.name());
        if (fvv == null) {
            return null;
        }
        IValues mffcValues = fvv.get(ExtractorEnum.MFCC_EXTRACTOR.name());

        if (mffcValues == null) {
            return null;
        }
        Assert.isTrue(mffcValues.size() > 0, "MFCC is not calculated. Size {0} ", mffcValues.size());
        SignalSegment aSegment = new SignalSegment();
        aSegment.setMarker(theMarker);
        aSegment.getFeatureFrameVectorValuesMap().put(ExtractorEnum.MFCC_EXTRACTOR.name(),
                new FrameVectorValuesHolder((FrameVectorValues) mffcValues));
        return aSegment;
    }

    public void setExtractorReaderService(WorkExtractorReaderService extractorReaderService) {
        this.extractorReaderService = extractorReaderService;
    }

    public void setCorpusService(CorpusService corpusService) {
        this.corpusService = corpusService;
    }

    public WorkExtractorReaderService getExtractorReaderService() {
        return extractorReaderService;
    }

    public CorpusService getCorpusService() {
        return corpusService;
    }


    public long getDelta() {
        return delta;
    }

    public void setDelta(long delta) {
        this.delta = delta;
    }
	public List<SignalSegment> getKeySegmentList() {
		return keySegmentList;
	}
	public void setKeySegmentList(List<SignalSegment> keySegmentList) {
		this.keySegmentList = keySegmentList;
	}
	public void addKeySegment(SignalSegment keySegment) {
		if(keySegmentList == null){
			keySegmentList = new ArrayList<>();
		}
		keySegmentList.add(keySegment);
	}

	public void setSegmentRecognitionThresholdService(
			SegmentRecognitionThresholdService segmentRecognitionThresholdService) {
		this.segmentRecognitionThresholdService = segmentRecognitionThresholdService;
	}
    
    
}
