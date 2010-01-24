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

import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IClassifier;
import org.spantus.segment.offline.SimpleDecisionSegmentatorParam;
import org.spantus.segment.offline.SimpleDecisionSegmentatorServiceImpl;
import org.spantus.segment.offline.SimpleSegmentatorServiceImpl;

public class SimpleDecisionSegmentatorTest extends SegmentatorTest {

	protected static Float[] silence = new Float[]{0f, 0f, 0f, 0f};
	protected static Float[] noise = new Float[]{0f, 1f, 0f };
	protected static Float[] errorShortSignal = new Float[]{0f, 1f, 0f};
	protected static Float[] signal = new Float[]{1f, 1f, 1f, 1f, 1f};

	
	public void testDecisionSegmentator(){
		SimpleDecisionSegmentatorServiceImpl segmentator = new SimpleDecisionSegmentatorServiceImpl();
		SimpleDecisionSegmentatorParam param = new SimpleDecisionSegmentatorParam();
		param.setMinSpace(20L);
		param.setMinLength(40L);
		segmentator.setSegmentator(new SimpleSegmentatorServiceImpl());

		Integer[][] markersData1 = new Integer[][]{
				{100, 200}, //signal
				{300, 320}, //noise
				{400,420},{440,540},//noise in the segment front
				{700,800},{820,840},//noise in the segment end
				{940, 1040}, //signal
				};
		Integer[][] markersDataExpexted = new Integer[][]{{100, 200}, {400, 540}, {700, 840}, {940, 1040}};

		Set<IClassifier> classifiers = new HashSet<IClassifier>();
		classifiers.add(contsructClassifier(markersData1));

		MarkerSetHolder markerSet = segmentator.extractSegments(classifiers, param);
		assertEqualsMarkers("decision", markersDataExpexted, markerSet);
	}
	
}
