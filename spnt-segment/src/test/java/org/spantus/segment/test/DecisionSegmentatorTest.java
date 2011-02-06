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
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.segment.offline.BaseDecisionSegmentatorParam;
import org.spantus.segment.offline.BasicSegmentatorServiceImpl;
import org.spantus.segment.offline.OfflineSegmentatorServiceImpl;
import org.spantus.segment.offline.WaheedDecisionSegmentatorServiceImpl;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.segment.online.OnlineSegmentaitonService;

public class DecisionSegmentatorTest extends AbstractSegmentatorTest {

	public final static Integer[][] DATA_markersData1 = new Integer[][]{
			{100, 200}, //signal
			{300, 320}, //noise
			{400,425},{440,540},//noise in the segment front
			{700,800},{815,840},//noise in the segment end
			{940, 1040}, //signal
			};

	/**
	 * 
	 */
	public void testSimpleDecisionSegmentator(){
		OfflineSegmentatorServiceImpl segmentator = new OfflineSegmentatorServiceImpl();
		BaseDecisionSegmentatorParam param = new BaseDecisionSegmentatorParam();
		param.setMinSpace(20L);
		param.setMinLength(40L);
		segmentator.setSegmentator(new BasicSegmentatorServiceImpl());

		Integer[][] markersDataExpexted = new Integer[][]{{100, 200}, {400, 540}, {700, 840}, {940, 1040}};

		Set<IClassifier> classifiers = new HashSet<IClassifier>();
		classifiers.add(contsructClassifier(DATA_markersData1));

		MarkerSetHolder markerSetHolder = segmentator.extractSegments(classifiers, param);
		assertEqualsMarkers("decision", markersDataExpexted, markerSetHolder, MarkerSetHolderEnum.word);
		MarkerSet phones = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		assertEquals(7, phones.getMarkers().size());
	}
	/**
	 * 
	 */
	public void testWaheedSegmentator(){
		WaheedDecisionSegmentatorServiceImpl segmentator = new WaheedDecisionSegmentatorServiceImpl();
		BaseDecisionSegmentatorParam param = new BaseDecisionSegmentatorParam();
		param.setMinSpace(20L);
		param.setMinLength(40L);
		segmentator.setSegmentator(new BasicSegmentatorServiceImpl());

		Integer[][] markersDataExpected = new Integer[][]{{100, 200}, {400, 540}, {700, 840}, {940, 1040}};

		Set<IClassifier> classifiers = new HashSet<IClassifier>();
		classifiers.add(contsructClassifier(DATA_markersData1));

		MarkerSetHolder markerSetHolder = segmentator.extractSegments(classifiers, param);
		assertEqualsMarkers("decision", markersDataExpected, markerSetHolder, MarkerSetHolderEnum.word);
		MarkerSet phones = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		assertEquals(7, phones.getMarkers().size());
	}
	/**
	 * 
	 */
	public void testOnlineSegmentator(){
		OnlineSegmentaitonService segmentator = new OnlineSegmentaitonService();
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinSpace(20L);
		param.setMinLength(40L);
		param.setExpandStart(0L);
		param.setExpandEnd(0L);

		Integer[][] markersDataExpexted = new Integer[][]{{100, 201}, {400, 541}, {700, 841}, {940, 1042}};

		Set<IClassifier> classifiers = new HashSet<IClassifier>();
		classifiers.add(contsructClassifier(DATA_markersData1));

		MarkerSetHolder markerSetHolder = segmentator.extractSegments(classifiers, param);
		assertEqualsMarkers("decision", markersDataExpexted, markerSetHolder, MarkerSetHolderEnum.word);
		MarkerSet phones = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		assertEquals(7, phones.getMarkers().size());
	}
	
}
