/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.dto;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.spantus.core.beans.RecognitionResult;
import org.spantus.logger.Logger;

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

/**
 *
 * @author mondhs
 */
public class SpottingSyllableCtx {

    private static final Logger LOG = Logger.getLogger(SpottingSyllableCtx.class);
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
    Double maxDelta = -Double.MAX_VALUE;
    Double minFirstMfccValue = Double.MAX_VALUE;
    Map<Long, String> syllableNameMap = new LinkedHashMap<>();
    Map<Long, Double> minMfccMap = new LinkedHashMap<>();
    Map<Long, Double> maxDeltaMap = new LinkedHashMap<>();
    Long minFirstMfccStart = null;
    Long maxDeltaStart = null;
    private Map<Long, List<RecognitionResult>> resultMap = new HashMap<Long, List<RecognitionResult>>();

    public Double getMaxDelta() {
        return maxDelta;
    }

    public void setMaxDelta(Double maxDelta) {
        this.maxDelta = maxDelta;
    }

    public Double getMinFirstMfccValue() {
        return minFirstMfccValue;
    }

    public void setMinFirstMfccValue(Double minFirstMfccValue) {
        this.minFirstMfccValue = minFirstMfccValue;
    }

    public Map<Long, String> getSyllableNameMap() {
        return syllableNameMap;
    }

    public void setSyllableNameMap(Map<Long, String> syllableNameMap) {
        this.syllableNameMap = syllableNameMap;
    }

    public Map<Long, Double> getMinMfccMap() {
        return minMfccMap;
    }

    public void setMinMfccMap(Map<Long, Double> minMfccMap) {
        this.minMfccMap = minMfccMap;
    }

    public Map<Long, Double> getMaxDeltaMap() {
        return maxDeltaMap;
    }

    public void setMaxDeltaMap(Map<Long, Double> maxDeltaMap) {
        this.maxDeltaMap = maxDeltaMap;
    }

    public Long getMinFirstMfccStart() {
        return minFirstMfccStart;
    }

    public void setMinFirstMfccStart(Long minFirstMfccStart) {
        this.minFirstMfccStart = minFirstMfccStart;
    }

    public Long getMaxDeltaStart() {
        return maxDeltaStart;
    }

    public void setMaxDeltaStart(Long maxDeltaStart) {
        this.maxDeltaStart = maxDeltaStart;
    }

    public Map<Long, List<RecognitionResult>> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<Long, List<RecognitionResult>> resultMap) {
        this.resultMap = resultMap;
    }

    
    
    
    public void printDeltas() {
        printMap("printDeltas", maxDeltaMap);
    }

    public void printMFCC() {
        printMap("printMFCC", minMfccMap);
    }

    public void printSyllableFrequence() {
        SetMultimap<Long, String> multimap = Multimaps.forMap(syllableNameMap);
        final Multimap<String, Long> inverse = Multimaps.invertFrom(multimap, HashMultimap.<String, Long>create());
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
    public <T, K> Multimap<T, K> sortedByDescendingFrequency(Multimap<T, K> multimap) {
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
                new Function<Map.Entry<Long, Double>, String>() {
                    @Override
                    public String apply(Map.Entry<Long, Double> input) {
                        return "" + input.getKey() + ";" + input.getValue() + ";" + syllableNameMap.get(input.getKey());
                    }
                }));
        LOG.error(mapName + " \n" + recognized);
    }
}
