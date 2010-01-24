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
package org.spantus.segment.online.test;

/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.11.27
 *
 */
public class MockSegmentatorExtractorConfig extends MockExtractorConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int getBufferSize() {
		return 100;
	}


	public int getFrameSize() {
		return 0;
	}

	public float getSampleRate() {
		return 1;
	}

	public int getWindowOverlap() {
		return 1;
	}

	public int getWindowSize() {
		return 2;
	}



}
