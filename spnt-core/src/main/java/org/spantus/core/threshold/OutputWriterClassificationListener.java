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

import org.spantus.logger.Logger;
/**
 * Output writer listener
 * @author Mindaugas Greibus
 *
 */
public class OutputWriterClassificationListener implements
		IClassificationListener {
	
	private Logger log = Logger.getLogger(OutputWriterClassificationListener.class);
	private BufferedWriter out = null;
	/**
	 * 
	 */
	public void onSegmentedStarted(SegmentEvent event) {
		try {
			getWriter().write("H");
			getWriter().flush();
		} catch (IOException e) {
			log.error(e);
		}

	}
	/**
	 * 
	 */
	public void onSegmentedEnded(SegmentEvent event) {
		try {
			getWriter().write("H");
			getWriter().flush();
		} catch (IOException e) {
			log.error(e);
		}

	}
	/**
	 * 
	 */
	public void onSegmentedProcessed(SegmentEvent event) {
		// do nothing
	}
	/**
	 * 
	 */
	public void registered(String id) {
		// do nothing
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	protected Writer getWriter() throws IOException {
		if (out == null) {
			out = new BufferedWriter(new PrintWriter(System.out));
		}
		return out;
	}

}
