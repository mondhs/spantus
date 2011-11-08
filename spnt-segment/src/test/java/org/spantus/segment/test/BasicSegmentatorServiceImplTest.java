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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.offline.BasicSegmentatorServiceImpl;

public class BasicSegmentatorServiceImplTest extends AbstractSegmentatorTest {
	/**
	 * 
	 */
	public void testSingleSegmentator(){
		ISegmentatorService segmentator = new BasicSegmentatorServiceImpl();
		Set<IClassifier> classifiers = new HashSet<IClassifier>();
		Integer[][] markersData = new Integer[][]{{100, 200}, {300, 400}, {500, 600}};
		classifiers.add(contsructClassifier(markersData));
		MarkerSetHolder markerSetHolder = segmentator.extractSegments(classifiers);
		assertEqualsMarkers("3 datasets", markersData, markerSetHolder, MarkerSetHolderEnum.phone);
	}
	/**
	 * 
	 */
	public void _testDoubleSegmentator(){
		ISegmentatorService segmentator = new BasicSegmentatorServiceImpl();
		Set<IClassifier> classifiers = new HashSet<IClassifier>();
		
		Integer[][] markersData1 = new Integer[][]{{100, 205}, {300, 400}, {495, 600}};
		Integer[][] markersData2 = new Integer[][]{{100, 200}, {295, 405}, {500, 605}};
		Integer[][] markersDataExpexted = new Integer[][]{{99, 206}, {299, 401}, {494, 601}};

		
		classifiers.add(contsructClassifier(markersData1));
		classifiers.add(contsructClassifier(markersData2));

		MarkerSetHolder markerSetHolder = segmentator.extractSegments(classifiers);
		assertEqualsMarkers("3 datasets", markersDataExpexted, markerSetHolder, MarkerSetHolderEnum.phone);
	}
	/**
	 * 
	 */
	public void _testTripleSegmentator(){
		ISegmentatorService segmentator = new BasicSegmentatorServiceImpl();
		Set<IClassifier> classifiers = new HashSet<IClassifier>();
		
//		Float[] statesF1 = new Float[]{0f, 0f, 1f, 1f, 0f, 0f, 0f, 0f};
//		Float[] statesF2 = new Float[]{0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f};
//		Float[] statesF3 = new Float[]{0f, 0f, 1f, 1f, 0f, 0f, 1f, 0f};
		Integer[][] markers1 = new Integer[][]{{100, 205}, {300, 400}, {495, 600}};
		Integer[][] markers2 = new Integer[][]{{100, 200}, {295, 405}, {500, 605}};
		Integer[][] markersDataExpexted = new Integer[][]{{100, 205}, {300, 400}, {495, 600}};


		classifiers.add(contsructClassifier(markers1));
		classifiers.add(contsructClassifier(markers2));
		classifiers.add(contsructClassifier(markers1));
		
		MarkerSetHolder markerSet = segmentator.extractSegments(classifiers);
		assertEqualsMarkers("3 datasets", markersDataExpexted, markerSet, MarkerSetHolderEnum.phone);
	}
	
	
	public void testVote(){
		//given
		BasicSegmentatorServiceImpl impl = new BasicSegmentatorServiceImpl();
		Double[] v1 = new Double[]{0D,0D,0D};
		Double[] v2 = new Double[]{.4D,.5D,.6D};
		Double[] v3 = new Double[]{1D,1D,1D};
		//when
		Double karimi1 = impl.calculateVoteResult(Arrays.asList(v1));
		Double karimi2 = impl.calculateVoteResult(Arrays.asList(v2));
		Double karimi3 = impl.calculateVoteResult(Arrays.asList(v3));
		//then
		Assert.assertEquals(0D, karimi1);
		Assert.assertEquals(0.5D, karimi2);
		Assert.assertEquals(1D, karimi3);
	}

}
