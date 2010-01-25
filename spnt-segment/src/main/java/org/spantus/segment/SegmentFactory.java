/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.segment;

import org.spantus.segment.offline.SimpleDecisionSegmentatorServiceImpl;
import org.spantus.segment.offline.SimpleSegmentatorServiceImpl;
import org.spantus.segment.offline.WaheedDecisionSegmentatorServiceImpl;
import org.spantus.segment.online.OnlineSegmentaitonService;
/**
 * Factory for segmentation service
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Jan 25, 2010
 *
 */
public abstract class SegmentFactory {
	
	public enum SegmentatorServiceEnum{basic, offline, online, waheed};
	
	public static ISegmentatorService defaultSegmentator;
	/**
	 * Default segmentation service is offline based on rule base
	 * @return
	 */
	public static ISegmentatorService createSegmentator(){
		return createSegmentator(SegmentatorServiceEnum.offline.name());
	}
	/**
	 * create selected segementator
	 * 
	 * @param segmentatorService this is {@link SegmentatorServiceEnum}
	 * @return
	 */
	public static ISegmentatorService createSegmentator(String segmentatorService){
		ISegmentatorService segmentator = null;
		SegmentatorServiceEnum segmentatorServiceEnum = SegmentatorServiceEnum.valueOf(segmentatorService);
		switch (segmentatorServiceEnum) {
		case basic:
			segmentator = new SimpleDecisionSegmentatorServiceImpl();
			break;
		case online:
			segmentator = new OnlineSegmentaitonService();
			break;
		case offline:
			SimpleDecisionSegmentatorServiceImpl offlineSegmentator = new SimpleDecisionSegmentatorServiceImpl();
			offlineSegmentator.setSegmentator(new SimpleSegmentatorServiceImpl());
			segmentator = offlineSegmentator;
			break;
		case waheed:
			WaheedDecisionSegmentatorServiceImpl waheed = new WaheedDecisionSegmentatorServiceImpl();
			waheed.setSegmentator(new SimpleSegmentatorServiceImpl());
			segmentator = waheed;
			break;
		default:
			break;
		}
		return segmentator;
	}
}
