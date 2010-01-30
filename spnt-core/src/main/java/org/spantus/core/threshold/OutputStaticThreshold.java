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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.spantus.core.marker.Marker;
import org.spantus.logger.Logger;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.11.27
 * 
 */
public class OutputStaticThreshold extends StaticThreshold {

	private Logger log = Logger.getLogger(getClass());

//	private Float lastVal = Float.valueOf(0f);
	private BufferedWriter out = null;


	/**
	 * custom logic on segment found event
	 * 
	 * @param marker
	 */
	@Override
	protected void onSegmentedStarted(Marker marker) {
		super.onSegmentedStarted(marker);
		try {
			getWriter().write("H");
			getWriter().flush();
		} catch (IOException e) {
			log.error(e);
		}

	}

	/**
	 * custom logic on segment found event
	 * 
	 * @param marker
	 */
	@Override
	protected void onSegmentedEnded(Marker marker) {
		super.onSegmentedStarted(marker);
		try {
			getWriter().write("L");
			getWriter().flush();
		} catch (IOException e) {
			log.error(e);
		}


	}


	protected Writer getWriter() throws IOException {
		if (out == null) {
			out = new BufferedWriter(new PrintWriter(System.out));
		}
		return out;
	}

}
