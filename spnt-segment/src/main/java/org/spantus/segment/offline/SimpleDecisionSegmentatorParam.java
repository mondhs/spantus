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

import java.text.MessageFormat;

import org.spantus.segment.SegmentatorParam;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 */
public class SimpleDecisionSegmentatorParam extends SegmentatorParam {
	/**
	 * Minimum length in milliseconds, when segment can be accepted as valid.
	 */
	private Long minLength;
	/**
	 * Minimum distance in milliseconds between two segments, when segments can
	 * be accepted as two different segments
	 */
	private Long minSpace;

	/**
	 * get minimum length of a segment in milliseconds.
	 */
	public Long getMinLength() {
		if (minLength == null) {
			minLength = 0L;
		}
		return minLength;
	}

	/**
	 * set minimum length of a segment in milliseconds.
	 */
	public void setMinLength(Long minSignalLength) {
		this.minLength = minSignalLength;
	}

	/**
	 * get minimum distance between two segments in milliseconds.
	 */
	public Long getMinSpace() {
		if (minSpace == null) {
			minSpace = 0L;
		}
		return minSpace;
	}
	/**
	 *  set minimum distance between two segments in milliseconds.
	 */
	public void setMinSpace(Long minSpace) {
		this.minSpace = minSpace;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0}[minLength: {1}; minSpace: {2}]", SimpleDecisionSegmentatorParam.class.getSimpleName(),getMinLength(),getMinSpace());
	}

}
