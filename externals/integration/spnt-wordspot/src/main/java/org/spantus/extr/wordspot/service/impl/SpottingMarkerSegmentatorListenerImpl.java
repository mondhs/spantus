package org.spantus.extr.wordspot.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.spantus.core.IValues;

import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorInputReaderAware;
import org.spantus.core.marker.Marker;
import org.spantus.extr.wordspot.service.WordSpottingListener;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import scikit.util.Pair;

/**
 *
 * @author Mindaugas Greibus
 * @since 0.3 Created: May 7, 2012
 *
 */
public class SpottingMarkerSegmentatorListenerImpl extends RecognitionMarkerSegmentatorListenerImpl {
    
    private static final Logger LOG = Logger.getLogger(SpottingMarkerSegmentatorListenerImpl.class);    
    private WordSpottingListener wordSpottingListener;
    private String sourceId = "sourceId";

     protected SpottingMarkerSegmentatorListenerImpl() {
         LOG.debug("Init");
         sourceId = sourceId + hashCode(); 
     }
    
    public SpottingMarkerSegmentatorListenerImpl(WordSpottingListener wordSpottingListener) {
        this();
        this.wordSpottingListener = wordSpottingListener;
    }
    
    @Override
    protected boolean processEndedSegment(SignalSegment signalSegment) {
        super.processEndedSegment(signalSegment);
        //done in purpose
        return false;
    }

    @Override
    protected RecognitionResult recalculateAndMatch(SignalSegment signalSegment) {
        Long initialStart = signalSegment.getMarker().getStart();
        Marker aMarker = signalSegment.getMarker().clone();
        Long availableLength = getExtractorInputReader().getAvailableSignalLengthMs();
        List<RecognitionResult> result = null;
        SpottingSyllableCtx ctx = new SpottingSyllableCtx();
        for (Long i = -50L; i < 150L; i+=10L) {
            aMarker.setStart(initialStart+i);
            Map<String, IValues> mapValues = recalculateFeatures( aMarker);
            result = getCorpusService().findMultipleMatchFull(mapValues);
            Boolean processes =processResult(ctx, result, aMarker);
            if(processes == null){
                break;
            }else if(Boolean.FALSE.equals(processes)){
                continue;
            }
        }
        if(ctx.maxDeltaStart != null){
//            signalSegment.getMarker().setStart(ctx.maxDeltaStart);
        }
        if(ctx.minFirstMfccStart != null){
              signalSegment.getMarker().setStart(ctx.minFirstMfccStart);
              result = ctx.resultMap.get(signalSegment.getMarker().getStart());
        }
//        ctx.printDeltas();
//        ctx.printMFCC();
          wordSpottingListener.foundSegment(sourceId, signalSegment, result);
        return null;
    }
        /**
         * 
         * @param ctx
         * @param result
         * @param aMarker
         * @return 
         */
        private Boolean processResult(SpottingSyllableCtx ctx, List<RecognitionResult> result, Marker aMarker) {
            Pair<Double,Double> deltaAndFirstMfccValue = findFirstTwoDela(result);
            Double firstMfccValue = deltaAndFirstMfccValue.fst();
            Double delta = deltaAndFirstMfccValue.snd();
            
            ctx.resultMap.put(aMarker.getStart(), result);
            
            if(firstMfccValue == null){
                //do nothing
            }else{
                String name = result.get(0).getInfo().getName();
                ctx.minMfccMap.put(aMarker.getStart(), new Pair<Double, String>(firstMfccValue,name));
                if(ctx.minFirstMfccValue.doubleValue()>firstMfccValue.doubleValue()){
                    ctx.minFirstMfccValue = firstMfccValue;
                    ctx.minFirstMfccStart = aMarker.getStart();
                }
            }
            
            if(result == null || result.isEmpty()){
                return null;//most probably we will not fine anythig here
            }if(delta == null && result.size()==1){
                return false;//only one hit, keep searching
            }else {
                String name = result.get(0).getInfo().getName();
                 ctx.maxDeltaMap.put(aMarker.getStart(), new Pair<Double, String>(delta,name));
                if(ctx.maxDelta.doubleValue()<delta.doubleValue()){
                    ctx.maxDelta = delta;
                    ctx.maxDeltaStart = aMarker.getStart();
                }
            }
            return true;
    }
    
    public Pair<Double,Double> findFirstTwoDela(List<RecognitionResult> result) {
        RecognitionResult first = null;
        RecognitionResult second = null;
        for (RecognitionResult recognitionResult : result) {
            if (first == null) {
                first = recognitionResult;
            } else if (second == null) {
                second = recognitionResult;
            }
            LOG.debug("[match] matched segment: {0} [{1}]", recognitionResult.getInfo().getName(), recognitionResult.getScores());
        }
        Double delta = null;
        Double firstMfccScore = null;
        Double firstMfccValue = null;
        if (first != null && second != null) {
            firstMfccScore = first.getScores().get(ExtractorEnum.MFCC_EXTRACTOR.name());
            firstMfccValue = first.getDetails().getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name());
            Double secondMfccScore = second.getScores().get(ExtractorEnum.MFCC_EXTRACTOR.name());
            delta = secondMfccScore - firstMfccScore;
        }
        return new Pair<Double,Double>(firstMfccValue, delta);
    }
    
    
//    @Override
//    protected String match(SignalSegment signalSegment) {
//        List<RecognitionResult> result = getCorpusService().findMultipleMatchFull(signalSegment.findAllFeatures());
//        LOG.debug("[match] segment for mathcing: {0}", signalSegment.getMarker());
//        RecognitionResult first = null;
//        RecognitionResult second = null;
//                
//        for (RecognitionResult recognitionResult : result) {
//            if(first == null){
//                first = recognitionResult;
//            }else if(second == null){
//                second = recognitionResult;
//            }
//            LOG.debug("[match] matched segment: {0} [{1}]", recognitionResult.getInfo().getName(), recognitionResult.getScores());
//        }
//        Double delta = null;
//        if(first != null && second != null){
//            Double firstMfccVaue = first.getScores().get(ExtractorEnum.MFCC_EXTRACTOR.name());
//            Double secondMfccVaue = second.getScores().get(ExtractorEnum.MFCC_EXTRACTOR.name());
//            delta = secondMfccVaue -  firstMfccVaue;
//        }
//        
//        if(result.size()==0){
//             LOG.error("[match] result not found for " + signalSegment);
//        }
////        RecognitionResult result1 = getCorpusService().matchByCorpusEntry(signalSegment);
//        wordSpottingListener.foundSegment(sourceId, signalSegment, result);
//        return null;
//    }
    
    public WordSpottingListener getWordSpottingListener() {
        return wordSpottingListener;
    }
    
    public void setWordSpottingListener(WordSpottingListener wordSpottingListener) {
        this.wordSpottingListener = wordSpottingListener;
    }
    
    @Override
    public void setExtractorInputReader(IExtractorInputReader extractorInputReader) {
        super.setExtractorInputReader(extractorInputReader);
        if(wordSpottingListener instanceof IExtractorInputReaderAware){
            ((IExtractorInputReaderAware)wordSpottingListener).setExtractorInputReader(extractorInputReader);
        }
    }
    
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public class SpottingSyllableCtx{
        Double maxDelta = -Double.MAX_VALUE;
        Double minFirstMfccValue = Double.MAX_VALUE;
        Map<Long, Pair<Double, String>> minMfccMap = new LinkedHashMap<Long, Pair<Double, String>>();
        Map<Long, Pair<Double, String>> maxDeltaMap = new LinkedHashMap<Long, Pair<Double, String>>();
        Long minFirstMfccStart = null;
        Long maxDeltaStart = null;
        private Map<Long,List<RecognitionResult>> resultMap = new HashMap<Long,List<RecognitionResult>>();
        
        public void printDeltas(){
            printMap("printDeltas", maxDeltaMap);
        }
        public void printMFCC(){
            printMap("printMFCC", minMfccMap);
        }
        
        protected void printMap(String mapName, Map<Long, Pair<Double, String>> theMap){
            Joiner joiner = Joiner.on("\n").skipNulls();
            String recognized = joiner.join(Collections2.transform(theMap.entrySet(), 
                    new Function<Entry<Long, Pair<Double, String>>,String>(){
                @Override
                public String apply(Entry<Long, Pair<Double, String>> input) {
                   return ""+input.getKey() + ";"+input.getValue().fst()+";" + input.getValue().snd();
                }
               }));
            LOG.error(mapName +" \n"+recognized);
        }
        
    }

    
}
