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
package org.spantus.core.threshold;

import org.spantus.logger.Logger;
/**
 * Logger listener
 * 
 * @author Mindaugas Greibus
 *
 */
public class LogClassificationListener implements IClassificationListener {
	Logger log = Logger.getLogger(LogClassificationListener.class);
	/**
	 * 
	 */
	public void onSegmentStarted(SegmentEvent event) {
		log.debug("[onSegmentStarted]{0}: {1}", event.getExtractorId(), event.getMarker());
	}
	/**
	 * 
	 */
	public void onSegmentEnded(SegmentEvent event) {
		log.debug("[onSegmentEnded]{0}: {1}",event.getExtractorId(), event.getMarker());
	}
	/**
	 * 
	 */
	public void onSegmentProcessed(SegmentEvent event) {
		log.debug("[onSegmentProcessed]{0}: {1}", event.getExtractorId(), event.getMarker());
	}
	public void onNoiseProcessed(SegmentEvent event) {
		log.debug("[onNoiseProcessed]{0}: {1}", event.getExtractorId(), event.getMarker());		
	}
	/**
	 * 
	 */
	public void registered(String id) {
		// do nothing
	}
	

}
