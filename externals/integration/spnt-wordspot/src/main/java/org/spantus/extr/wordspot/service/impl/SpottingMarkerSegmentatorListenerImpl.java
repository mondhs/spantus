package org.spantus.extr.wordspot.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.primitives.Ints;
import java.lang.String;
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
        for (Long i = -50L; i < 100L; i += 10L) {
            aMarker.setStart(initialStart + i);
            Map<String, IValues> mapValues = recalculateFeatures(aMarker);
            result = getCorpusService().findMultipleMatchFull(mapValues);
            Boolean processes = processResult(ctx, result, aMarker);
            if (processes == null) {
                break;
            } else if (Boolean.FALSE.equals(processes)) {
                continue;
            }
        }
        if (ctx.maxDeltaStart != null) {
//            signalSegment.getMarker().setStart(ctx.maxDeltaStart);
        }
        if (ctx.minFirstMfccStart != null) {
            //recalculate if current index not in the range of common syllables(same names);
            Long tmpMinFirstMfccStart = findMinMfccForMostCommon(ctx.syllableNameMap, ctx.minMfccMap);
            if(tmpMinFirstMfccStart == null){
                tmpMinFirstMfccStart = ctx.minFirstMfccStart;
            }
                
            signalSegment.getMarker().setStart(tmpMinFirstMfccStart);
            result = ctx.resultMap.get(signalSegment.getMarker().getStart());
        }
//        ctx.printDeltas();
//        ctx.printMFCC();
//        ctx.printSyllableFrequence();
        if (LOG.isDebugMode() && signalSegment.getMarker().getStart() > SpottingDebug.EXPECTED_BREAKPOINT) {
            LOG.debug("break point should go here");
        }

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
        Pair<Double, Double> deltaAndFirstMfccValue = findFirstTwoDela(result);
        Double firstMfccValue = deltaAndFirstMfccValue.fst();
        Double delta = deltaAndFirstMfccValue.snd();

        ctx.resultMap.put(aMarker.getStart(), result);

        if (result == null || result.isEmpty()) {
            ctx.syllableNameMap.put(aMarker.getStart(), "");
            return null;//most probably we will not fine anythig here
        }
        
        String name = result.get(0).getInfo().getName();
        ctx.syllableNameMap.put(aMarker.getStart(), name);

        if (firstMfccValue == null) {
            //do nothing
        } else {
            ctx.minMfccMap.put(aMarker.getStart(), firstMfccValue);
            if (ctx.minFirstMfccValue.doubleValue() > firstMfccValue.doubleValue()) {
                ctx.minFirstMfccValue = firstMfccValue;
                ctx.minFirstMfccStart = aMarker.getStart();
            }
        }


        if (delta == null && result.size() == 1) {
            return false;//only one hit, keep searching
        } else {
            ctx.maxDeltaMap.put(aMarker.getStart(), delta);
            if (ctx.maxDelta.doubleValue() < delta.doubleValue()) {
                ctx.maxDelta = delta;
                ctx.maxDeltaStart = aMarker.getStart();
            }
        }
        return true;
    }

    public Pair<Double, Double> findFirstTwoDela(List<RecognitionResult> result) {
        RecognitionResult first = null;
        RecognitionResult second = null;
        for (RecognitionResult recognitionResult : result) {
            if (first == null) {
                first = recognitionResult;
            } else if (second == null) {
                second = recognitionResult;
                break;
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
        return new Pair<Double, Double>(firstMfccValue, delta);
    }

    public WordSpottingListener getWordSpottingListener() {
        return wordSpottingListener;
    }

    public void setWordSpottingListener(WordSpottingListener wordSpottingListener) {
        this.wordSpottingListener = wordSpottingListener;
    }

    @Override
    public void setExtractorInputReader(IExtractorInputReader extractorInputReader) {
        super.setExtractorInputReader(extractorInputReader);
        if (wordSpottingListener instanceof IExtractorInputReaderAware) {
            ((IExtractorInputReaderAware) wordSpottingListener).setExtractorInputReader(extractorInputReader);
        }
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    private Long findMinMfccForMostCommon(Map<Long, String> syllableNameMap, Map<Long, Double> minMfccMap) {
         SetMultimap<Long, String> multimap = Multimaps.forMap(syllableNameMap);
         Multimap<String, Long> inverse = Multimaps.invertFrom(multimap, HashMultimap.<String,Long> create());
         if(inverse.keySet().size()==1){
             //assume already calculated during regular iteration.
             return null;   
         }
         String commonElementKey = inverse.keySet().iterator().next();
         Long minArg = Long.MAX_VALUE; 
         Double minVal = Double.MAX_VALUE; 
         for (Long entry : inverse.get(commonElementKey)) {
            if(minMfccMap.get(entry) < minVal){
                minArg = entry;
                minVal = minMfccMap.get(minArg);
            } 
         }
         return minArg;
    }

    public class SpottingSyllableCtx {

        Double maxDelta = -Double.MAX_VALUE;
        Double minFirstMfccValue = Double.MAX_VALUE;
        Map<Long, String> syllableNameMap = new LinkedHashMap<>();
        Map<Long, Double> minMfccMap = new LinkedHashMap<>();
        Map<Long, Double> maxDeltaMap = new LinkedHashMap<>();
        Long minFirstMfccStart = null;
        Long maxDeltaStart = null;
        private Map<Long, List<RecognitionResult>> resultMap = new HashMap<Long, List<RecognitionResult>>();

        public void printDeltas() {
            printMap("printDeltas", maxDeltaMap);
        }

        public void printMFCC() {
            printMap("printMFCC", minMfccMap);
        }

        public void printSyllableFrequence() {
             SetMultimap<Long, String> multimap = Multimaps.forMap(syllableNameMap);
             final Multimap<String, Long> inverse = Multimaps.invertFrom(multimap, HashMultimap.<String,Long> create());
//            Multimap<String, List<Long>>mmap = sortedByDescendingFrequency(syllableFrequenceMMap);
            Joiner joiner = Joiner.on("\n").skipNulls();
            String recognized = joiner.join(Collections2.transform(inverse.keySet(),
                    new Function<String, String>() {
                        @Override
                        public String apply(String input) {
                            return "" + input + ";" + inverse.get(input).size();
                        }
                    }));
            LOG.error("syllableFrequenceMap" + " \n" + recognized);
        }

        /**
         * @return a {@link Multimap} whose entries are sorted by descending
         * frequency
         */
        public <T,K> Multimap<T, K>  sortedByDescendingFrequency(Multimap<T, K> multimap) {
            // ImmutableMultimap.Builder preserves key/value order
            ImmutableMultimap.Builder<T, K> result = ImmutableMultimap.builder();
            for (Multiset.Entry<T> entry : DESCENDING_COUNT_ORDERING.sortedCopy(multimap.keys().entrySet())) {
                result.putAll(entry.getElement(), multimap.get(entry.getElement()));
            }
            return result.build();
        }

        protected void printMap(String mapName, Map<Long, Double> theMap) {
            Joiner joiner = Joiner.on("\n").skipNulls();
            String recognized = joiner.join(Collections2.transform(theMap.entrySet(),
                    new Function<Entry<Long, Double>, String>() {
                        @Override
                        public String apply(Entry<Long, Double> input) {
                            return "" + input.getKey() + ";" + input.getValue() + ";" + syllableNameMap.get(input.getKey());
                        }
                    }));
            LOG.error(mapName + " \n" + recognized);
        }
    }
    /**
     * An {@link Ordering} that orders {@link Multiset.Entry Multiset entries}
     * by ascending count.
     */
    private static final Ordering<Multiset.Entry<?>> ASCENDING_COUNT_ORDERING = new Ordering<Multiset.Entry<?>>() {
        @Override
        public int compare(Multiset.Entry<?> left, Multiset.Entry<?> right) {
            return Ints.compare(left.getCount(), right.getCount());
        }
    };
    /**
     * An {@link Ordering} that orders {@link Multiset.Entry Multiset entries}
     * by descending count.
     */
    private static final Ordering<Multiset.Entry<?>> DESCENDING_COUNT_ORDERING = ASCENDING_COUNT_ORDERING.reverse();
}
