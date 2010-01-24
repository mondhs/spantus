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

	Logger log = Logger.getLogger(getClass());

	Float lastVal = Float.valueOf(0f);

	/**
	 * custom logic on segment found event
	 * 
	 * @param marker
	 */
	@Override
	protected void onSegmentedStarted(Marker marker) {
		fireSignal(marker.getStart() + marker.getLength());
	}

	/**
	 * custom logic on segment found event
	 * 
	 * @param marker
	 */
	@Override
	protected void onSegmentedEnded(Marker marker) {
		fireSilence(marker.getStart());

	}

	@Override
	public Float getCoef() {
		if (super.getCoef() == null) {
			setCoef(1.3f);// *30%
		}
		return super.getCoef();
	}

	protected void fireSilence(float time) {
		log.error("silence: " + time);
		try {
			getWriter().write("L");
			getWriter().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void fireSignal(float time) {
		log.debug("signal: " + time);
		try {
			getWriter().write("H");
			getWriter().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	BufferedWriter out = null;

	public Writer getWriter() throws IOException {
		if (out == null) {
			out = new BufferedWriter(new PrintWriter(System.out));
		}
		return out;
	}

}
