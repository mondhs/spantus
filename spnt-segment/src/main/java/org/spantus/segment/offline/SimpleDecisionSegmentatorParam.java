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
package org.spantus.segment.offline;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.spantus.segment.SegmentatorParam;

public class SimpleDecisionSegmentatorParam extends SegmentatorParam {

	private BigDecimal segmentLengthThreshold;
	
	private BigDecimal segmentsSpaceThreshold;

	public BigDecimal getSegmentLengthThreshold() {
		if(segmentLengthThreshold == null){
			segmentLengthThreshold = BigDecimal.valueOf(40f).setScale(1,RoundingMode.HALF_UP);
		}
		return segmentLengthThreshold;
	}

	public void setSegmentLengthThreshold(BigDecimal segmentLengthThreshold) {
		this.segmentLengthThreshold = segmentLengthThreshold;
	}

	public BigDecimal getSegmentsSpaceThreshold() {
		if(segmentsSpaceThreshold == null){
			segmentsSpaceThreshold = BigDecimal.valueOf(20f).setScale(1,RoundingMode.HALF_UP);
		}
		return segmentsSpaceThreshold;
	}

	public void setSegmentsSpaceThreshold(BigDecimal segmentsSpaceThreshold) {
		this.segmentsSpaceThreshold = segmentsSpaceThreshold;
	}

}
