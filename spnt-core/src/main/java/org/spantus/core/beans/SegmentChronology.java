/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.core.beans;

import java.util.*;
import org.spantus.utils.Assert;

/**
 *
 * @author mondhs
 * @since 0.3 created: 2012 07 01
 */
public class SegmentChronology<T> implements Map<Long, Set<T>>,
        Iterable<Map.Entry<Long, Set<T>>> {

    private HashMap<Long, Set<T>> chronologyMap = new HashMap<Long, Set<T>>();
    private Integer step;
    private Long startMoment = Long.MAX_VALUE;
    private Long lastKnownMoment = -Long.MAX_VALUE;
    private Integer fullThreshold;

    private TreeSet<Long> syncTime = new TreeSet<Long>();
    private TreeSet<Long> fullBinSet = new TreeSet<Long>();

    public SegmentChronology(int step, int fullThreshold) {
        this.step = step;
        this.fullThreshold = fullThreshold;
    }
    
     public boolean start(Long key, T value) {
         return false;
     }
     public boolean stop(Long key, T value) {
         return false;
     }

    public boolean add(Long key, T... values) {
        for (T value : values) {
            add(key, value);
        }
        return true;
    }
    
    public boolean add(Long key, T value) {
        if (key % step != 0) {
            throw new IllegalArgumentException("event time " + key + " should be alligned with step:"
                    + step);
        }
        startMoment = Math.min(startMoment, key);
        lastKnownMoment = Math.max(lastKnownMoment, key);
        Set<T> bin = chronologyMap.get(key);
        if (bin == null) {
            bin = new LinkedHashSet<T>();
            chronologyMap.put(key, bin);
        }
        boolean addedInd = bin.add(value);
        syncTime.add(key);
        if(bin.size()>=fullThreshold  ){
            fullBinSet.add(key);
        }
        return addedInd;
    }
    
    public int cleanUpTill(Long aTimeMoment) {
        if(aTimeMoment == null){
            return 0;
        }
        int removed = 0;
        for (Iterator<Long> it = syncTime.iterator(); it.hasNext();) {
            Long iTime = it.next();
            startMoment=iTime;
            if(iTime > aTimeMoment){
               break;
           }
           it.remove();
           chronologyMap.remove(iTime);
           fullBinSet.remove(iTime);
          
           removed++;
        }
        return removed;
    }
    
    @Override
    public Iterator<Entry<Long, Set<T>>> iterator() {
        return new SegmentCronologyIterator();
    }

    public Iterable<Entry<Long, Set<T>>> getPrior(Long priorMoment) {
        return new IterableImpl(priorMoment);
    }
    
    public Long getFirstFullBinIndex() {
        if(fullBinSet.isEmpty()){
         return null;
        }
        return fullBinSet.first();
    }
    
    public Entry<Long, Set<T>> getFirstFullBin() {
        Long firstIndex = getFirstFullBinIndex();
        if(firstIndex == null){
            return null;
        }
        return new EntryImpl(firstIndex, get(firstIndex));
    }
    
    public Long getLastFullBinIndex() {
     if(fullBinSet.isEmpty()){
         return null;
     }
     return fullBinSet.last();
    }
    public Entry<Long, Set<T>> getLastFullBin() {
        Long lastIndex = getLastFullBinIndex();
        if(lastIndex == null){
         return null;
        }
        return new EntryImpl(lastIndex, get(lastIndex));
    }

    public Long getDuration() {
        return lastKnownMoment - startMoment;
    }

    public Long getLastKnownMoment() {
        return lastKnownMoment;
    }

    public Long getStartMoment() {
        return startMoment;
    }

    @Override
    public int size() {
        return chronologyMap.size();
    }

    @Override
    public boolean isEmpty() {
        return chronologyMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return chronologyMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return chronologyMap.containsValue(value);
    }

    @Override
    public Set<T> get(Object key) {
        return chronologyMap.get(key);
    }

    @Override
    public Set<T> put(Long key, Set<T> value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<T> remove(Object key) {
        return chronologyMap.remove(key);
    }

    @Override
    public void clear() {
        chronologyMap.clear();
    }

    @Override
    public Set<Long> keySet() {
        return chronologyMap.keySet();
    }

    @Override
    public Collection<Set<T>> values() {
        return chronologyMap.values();
    }

    @Override
    public Set<Entry<Long, Set<T>>> entrySet() {
        return chronologyMap.entrySet();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Set<T>> m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFullThreshold(Integer fullThreshold) {
        this.fullThreshold = fullThreshold;
    }

    class EntryImpl implements Entry<Long, Set<T>> {

        private final Long entryKey;
        private final Set<T> entryValues;

        @Override
        public String toString() {
            return "EntryImpl{" + "entryKey=" + entryKey + ", entryValues=" + entryValues + '}';
        }

        
        public EntryImpl(Long entryKey, Set<T> entryValues) {
            this.entryKey = entryKey;
            this.entryValues = entryValues;
        }

        @Override
        public Long getKey() {
            return entryKey;
        }

        @Override
        public Set<T> getValue() {
            return entryValues;
        }

        @Override
        public Set<T> setValue(Set<T> value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private class IterableImpl implements Iterable<Entry<Long, Set<T>>> {
        private final Long priorMoment;
        private IterableImpl(Long priorMoment) {
            this.priorMoment = priorMoment;
        }
        @Override
        public Iterator<Entry<Long, Set<T>>> iterator() {
            return new SegmentCronologyIterator(priorMoment);
        }
    }

    private class SegmentCronologyIterator implements Iterator<Entry<Long, Set<T>>> {

        private Long iterKey = null;
        private final Long priorMoment;
        private Long iterateEndMoment;

        public SegmentCronologyIterator() {
            priorMoment = lastKnownMoment;
            iterateEndMoment = lastKnownMoment;
        }

        public SegmentCronologyIterator(Long priorMoment) {
            Assert.isTrue(priorMoment == null || 
                    (priorMoment > startMoment && priorMoment <= lastKnownMoment));
            this.priorMoment = priorMoment;
            this.iterateEndMoment = priorMoment;
        }

        @Override
        public boolean hasNext() {
            if(priorMoment == null){
                return false;
            }
            return iterKey == null || iterKey < iterateEndMoment;
        }

        @Override
        public Entry<Long, Set<T>> next() {
            if(iterKey == null){
                iterKey = startMoment;
            }else{
                iterKey += step;
            }
            Set<T> momentValues = chronologyMap.get(iterKey);
            if (momentValues == null) {
                momentValues = new LinkedHashSet<T>();
            }
            return new EntryImpl(iterKey, momentValues);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    
}
