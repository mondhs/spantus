/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.segment.test;

import java.util.HashSet;
import java.util.Set;

import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.IClassifier;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.offline.SimpleSegmentatorServiceImpl;

public class SimpleSegmentatorTest extends SegmentatorTest {
	
	public void testSingleSegmentator(){
		ISegmentatorService segmentator = new SimpleSegmentatorServiceImpl();
		Set<IClassifier> thresholds = new HashSet<IClassifier>();
		Float[] statesF = new Float[]{0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f,};
		thresholds.add(contsructThreshold(statesF));
		MarkerSet markerSet = segmentator.extractSegments(thresholds);
		assertEquals(2, markerSet.getMarkers().size());
	}

	public void testDoubleSegmentator(){
		ISegmentatorService segmentator = new SimpleSegmentatorServiceImpl();
		Set<IClassifier> thresholds = new HashSet<IClassifier>();
		
		Float[] statesF1 = new Float[]{0f, 0f, 1f, 1f, 0f, 0f, 0f, 0f};
		Float[] statesF2 = new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f};

		thresholds.add(contsructThreshold(statesF1));
		thresholds.add(contsructThreshold(statesF2));

		MarkerSet markerSet = segmentator.extractSegments(thresholds);
		assertEquals(0, markerSet.getMarkers().size());
	}

	public void testTripleSegmentator(){
		ISegmentatorService segmentator = new SimpleSegmentatorServiceImpl();
		Set<IClassifier> thresholds = new HashSet<IClassifier>();
		
		Float[] statesF1 = new Float[]{0f, 0f, 1f, 1f, 0f, 0f, 0f, 0f};
		Float[] statesF2 = new Float[]{0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f};
		Float[] statesF3 = new Float[]{0f, 0f, 1f, 1f, 0f, 0f, 1f, 0f};

		thresholds.add(contsructThreshold(statesF1));
		thresholds.add(contsructThreshold(statesF2));
		thresholds.add(contsructThreshold(statesF3));
		
		MarkerSet markerSet = segmentator.extractSegments(thresholds);
		assertEquals(2, markerSet.getMarkers().size());
	}

}
