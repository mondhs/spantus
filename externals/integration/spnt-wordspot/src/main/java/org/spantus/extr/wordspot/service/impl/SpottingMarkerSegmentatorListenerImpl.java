package org.spantus.extr.wordspot.service.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.primitives.Ints;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.spantus.core.IValues;

import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorInputReaderAware;
import org.spantus.core.marker.Marker;
import org.spantus.extr.wordspot.dto.SpottingSyllableCtx;
import org.spantus.extr.wordspot.service.SpottingListener;
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
    private SpottingListener wordSpottingListener;
    private String sourceId = "sourceId";
    private int operationCount;


    protected SpottingMarkerSegmentatorListenerImpl() {
        LOG.debug("Init");
        sourceId = sourceId + hashCode();
    }

    public SpottingMarkerSegmentatorListenerImpl(SpottingListener wordSpottingListener) {
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
        for (Long i = -50L; i < 150L; i += 20L) {
            aMarker.setStart(initialStart + i);
            if(aMarker.getEnd()>availableLength){
                break;
            }

            Map<String, IValues> mapValues = recalculateFeatures(aMarker);
            result = getCorpusService().findMultipleMatchFull(mapValues);
            this.operationCount++;
            Boolean processes = processResult(ctx, result, aMarker);
            if (processes == null) {
                break;
            } else if (Boolean.FALSE.equals(processes)) {
                continue;
            }
        }
        if (ctx.getMaxDeltaStart() != null) {
//            signalSegment.getMarker().setStart(ctx.maxDeltaStart);
        }
        if (ctx.getMinFirstMfccStart() != null) {
            //recalculate if current index not in the range of common syllables(same names);
            Long tmpMinFirstMfccStart = findMinMfccForMostCommon(ctx.getSyllableNameMap(), ctx.getMinMfccMap());
            if (tmpMinFirstMfccStart == null) {
                tmpMinFirstMfccStart = ctx.getMinFirstMfccStart();
            }

            aMarker.setStart(tmpMinFirstMfccStart);
            result = ctx.getResultMap().get(aMarker.getStart());
        }
//        ctx.printDeltas();
//        ctx.printMFCC();
//        ctx.printSyllableFrequence();
        if (LOG.isDebugMode() && aMarker.getStart() > SpottingDebug.EXPECTED_BREAKPOINT) {
            LOG.debug("break point should go here");
        }

        wordSpottingListener.foundSegment(sourceId, new SignalSegment(aMarker), result);
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

        ctx.getResultMap().put(aMarker.getStart(), result);

        if (result == null || result.isEmpty()) {
            ctx.getSyllableNameMap().put(aMarker.getStart(), "");
            return null;//most probably we will not fine anythig here
        }

        String name = result.get(0).getInfo().getName();
        ctx.getSyllableNameMap().put(aMarker.getStart(), name);

        if (firstMfccValue == null) {
            //do nothing
        } else {
            ctx.getMinMfccMap().put(aMarker.getStart(), firstMfccValue);
            if (ctx.getMinFirstMfccValue().doubleValue() > firstMfccValue.doubleValue()) {
                ctx.setMinFirstMfccValue(firstMfccValue);
                ctx.setMinFirstMfccStart(aMarker.getStart());
            }
        }


        if (delta == null && result.size() == 1) {
            return false;//only one hit, keep searching
        } else {
            ctx.getMaxDeltaMap().put(aMarker.getStart(), delta);
            if (ctx.getMaxDelta().doubleValue() < delta.doubleValue()) {
                ctx.setMaxDelta(delta);
                ctx.setMaxDeltaStart(aMarker.getStart());
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
            //LOG.debug("[findFirstTwoDela] matched segment: {0} [{1}]", recognitionResult.getInfo().getName(), recognitionResult.getScores());
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

    public SpottingListener getWordSpottingListener() {
        return wordSpottingListener;
    }

    public void setWordSpottingListener(SpottingListener wordSpottingListener) {
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
        Multimap<String, Long> inverse = Multimaps.invertFrom(multimap, HashMultimap.<String, Long>create());
        if (inverse.keySet().size() == 1) {
            //assume already calculated during regular iteration.
            return null;
        }
        ArrayList<Entry<String, Collection<Long>>> entries = new ArrayList<>(inverse.asMap().entrySet());


        Collections.sort(entries, new Comparator<Map.Entry<String, Collection<Long>>>() {
            @Override
            public int compare(Map.Entry<String, Collection<Long>> e1,
                    Map.Entry<String, Collection<Long>> e2) {
                return Ints.compare(e2.getValue().size(), e1.getValue().size());
            }
        });
        Entry<String, Collection<Long>> commonElement = entries.iterator().next();
        Long minArg = Long.MAX_VALUE;
        Double minVal = Double.MAX_VALUE;
        for (Long entry : commonElement.getValue()) {
            if (minMfccMap.get(entry) < minVal) {
                minArg = entry;
                minVal = minMfccMap.get(minArg);
            }
        }
        return minArg;
    }

    @Override
	public int getOperationCount() {
		return operationCount;
	}

}
