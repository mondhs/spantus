/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.core.beans;

import java.util.*;
import java.util.Map.Entry;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author as
 */
public class SegmentChronologyTest {

    public static final int FULL_THRESHOLD = 2;
    public static final int STEP = 10;
    public static final String value1 = "i1";
    public static final String value2 = "i2";
    public static final String value3 = "i3";
    private SegmentChronology<String> segmentChronology;

    @Before
    public void onSetup() {
        segmentChronology = new SegmentChronology<String>(STEP, FULL_THRESHOLD, new StringSegmentIdentifier());
    }

    @Test
    public void testAddAndGet() {
        //given
        Long time = 0L;
        //when
        boolean resultPut1 = segmentChronology.add(time, value1);
        boolean resultPut2 = segmentChronology.add(time, value2);
        boolean resultPut3 = segmentChronology.add(time, value3);
        Set<String> resultGet = segmentChronology.get(time);
        //then
        assertEquals(3, resultGet.size());

    }

    @Test
    public void testIterate() {
        //given
        for (long i = 0; i <= 100; i += STEP) {
            if (i != 0 && i % 30 == 0) {
                continue;
            }
            segmentChronology.add(i, value1);
            segmentChronology.add(i, value2);
            segmentChronology.add(i, value3);
        }
        //when
        Set<Long> result = new HashSet<Long>();
        for (Map.Entry<Long, Set<String>> entry : segmentChronology) {
            Long long1 = entry.getKey();
            result.add(long1);
        }

        //then
        assertEquals(11, result.size(), 0);
        assertEquals(100L, segmentChronology.getDuration(), 0);
    }

    @Test
    public void testGetPrior() {
        //given
        for (long i = 0; i < 100; i += STEP) {
            if (i != 0 && i % 20 == 0) {
                continue;
            }
            segmentChronology.add(i, value1);
            segmentChronology.add(i, value2);
            segmentChronology.add(i, value3);
        }
        //when
        Set<Long> uniqueResult = new HashSet<Long>();
        LinkedList<Entry<Long, Set<String>>> result = new LinkedList<Entry<Long, Set<String>>>();
        for (Map.Entry<Long, Set<String>> entry : segmentChronology.getPrior(50L)) {
            Long long1 = entry.getKey();
            uniqueResult.add(long1);
            result.add(entry);
        }

        //then
        assertEquals(6, result.size(), 0);
        assertEquals(0, result.getFirst().getKey(), 0);
        assertEquals(50, result.getLast().getKey(), 0);
    }

    
    @Test
    public void testCleanUpTo() {
        //given
        segmentChronology.add(0L, value1);
        segmentChronology.add(20L, value1, value2, value3);
        segmentChronology.add(30L, value1);
        segmentChronology.add(40L, value1, value2);
        segmentChronology.add(50L, value1, value2, value3);
        segmentChronology.add(10L, value1, value2);

        //when
        int removed = segmentChronology.cleanUpTill(30L);

        //then
        assertEquals(4, removed, 0);
        assertEquals(40, segmentChronology.getStartMoment(), 0);
        assertEquals(50, segmentChronology.getLastKnownMoment(), 0);
        assertEquals(10, segmentChronology.getDuration(), 0);
        
    }
    @Test
    public void testStartStop() {
        //given
        segmentChronology.start(0L, value1);
        segmentChronology.start(10L, value2);
        segmentChronology.start(20L, value3);
        segmentChronology.stop(30L, value2);
        segmentChronology.stop(40L, value1);
        segmentChronology.stop(50L, value3);
        segmentChronology.start(60L, value1);
        segmentChronology.start(70L, value2);
        segmentChronology.start(80L, value3);
        segmentChronology.stop(90L, value2);
        segmentChronology.stop(90L, value1);
        segmentChronology.stop(90L, value3);
        

        //when
        Entry<Long, Set<String>> firstBin = segmentChronology.getFirstFullBin();
        Entry<Long, Set<String>> lastBin = segmentChronology.getLastFullBin();

        //then
        assertEquals(10, firstBin.getKey(), 0);
        assertEquals(2,segmentChronology.getSegmentMap().get(firstBin.getKey()).size(), 0);
        assertEquals(90, lastBin.getKey(), 0);
        assertEquals(3, segmentChronology.getSegmentMap().get(lastBin.getKey()).size(), 0);
        
    }
}
