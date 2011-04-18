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
package org.spantus.work.services;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.logger.Logger;

/**
 * support for TextGrid of praat
 * 
 * 
 * <br>
 * format:<br>
 * File type = "ooTextFile"<br>
 * Object class = "TextGrid"<br>
 * <br>
 * xmin = 0<br>
 * xmax = 3.7828344671201814<br>
 * tiers? &lt;exists&gt;<br>
 * size = 2<br>
 * item []:<br>
 * item [1]:<br>
 * class = "IntervalTier"<br>
 * name = "Level 1"<br>
 * xmin = 0<br>
 * xmax = 3.7828344671201814<br>
 * intervals: size = 10<br>
 * intervals [1]:<br>
 * xmin = 0<br>
 * xmax = 0.9219844950605262<br>
 * text = ""<br>
 * intervals [2]:<br>
 * xmin = 0.9219844950605262<br>
 * xmax = 1.1692887650859252<br>
 * text = "v"<br>
 * 
 * @author Mindaugas Greibus
 * @since 0.0.1
 */
public class MarkerTextGridDao implements MarkerDao {

	Logger log = Logger.getLogger(MarkerTextGridDao.class);

	public static final String patternItem = "\\s+item \\[(\\d+)\\]:";
	public static final String patternIntervals = "\\s+intervals \\[(\\d+)\\]:";
	public static final String patternXmin = "\\s+xmin = (.+)";
	public static final String patternXmax = "\\s+xmax = (.+)";
	public static final String patternText = "\\s+text = (.*)";

	/**
	 * 
	 */
	public MarkerSetHolder read(File file) {
		ReadListener readListener = new ReadListener();
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(file);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String size="";
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// log.debug("read "+ strLine);
				if(strLine.matches("size = (\\d)\\s*")){
					String str = regexp("size = (\\d)\\s*", strLine);
					size =str;
				}else if (strLine.matches(patternItem)) {
					readListener.readItem(strLine,size);
				} else if (strLine.matches(patternIntervals)) {
					readListener.readIneterval(strLine);
				} else if (strLine.matches(patternXmin)) {
					readListener.readXmin(strLine);
				} else if (strLine.matches(patternXmax)) {
					readListener.readXmax(strLine);
				} else if (strLine.matches(patternText)) {
					readListener.readText(strLine);
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			log.error(e);
		}
		return readListener.getMarkerSetHolder();
	}

	public MarkerSetHolder read(InputStream inputStream) {
		throw new IllegalArgumentException("Not impl");
	}

	public void write(MarkerSetHolder holder, File file) {
		throw new IllegalArgumentException("Not impl");

	}

	public void write(MarkerSetHolder holder, OutputStream outputStream) {
		throw new IllegalArgumentException("Not impl");
	}

	public class ReadListener {
		MarkerSetHolder markerSetHolder = new MarkerSetHolder();
		MarkerSet markerSet;
		Marker marker;

		public void readItem(String index, String size) {
			markerSet = new MarkerSet();
			String str = regexp(patternItem, index);
			if ("1".equals(str)) {
				markerSet = new MarkerSet();
				markerSetHolder.getMarkerSets().put(
						MarkerSetHolderEnum.phone.name(), markerSet);
				marker = null;
//			} else if ("2".equals(str)) {
			} else if (size.equals(str)) {
				markerSet = new MarkerSet();
				markerSetHolder.getMarkerSets().put(
						MarkerSetHolderEnum.word.name(), markerSet);
				marker = null;
			}

			log.debug("[markerset]" + markerSetHolder);

		}

		public void readIneterval(String index) {
			String str = regexp(patternIntervals, index);
			marker = new Marker();
			markerSet.getMarkers().add(marker);
			marker.setLabel(str);
			log.debug("[marker]" + markerSet);

		}

		public void readXmin(String time) {
			String str = regexp(patternXmin, time);
			Float start = Float.valueOf(str) * 1000;
			if (marker != null) {
				marker.setStart(start.longValue());
			}
			log.debug("[marker start]" + marker);
		}

		public void readXmax(String time) {
			String str = regexp(patternXmax, time);
			Float end = Float.valueOf(str) * 1000;
			if (marker != null) {
				marker.setEnd(end.longValue());
			}
			log.debug("[marker end]" + marker);

		}

		public void readText(String text) {
			String str = regexp(patternText, text);
			str = str.replace("\"", "");
			if (marker != null) {
				if ("".equals(str.trim())) {
					markerSet.getMarkers().remove(marker);
				}
				marker.setLabel(str);
			}
			log.debug("[marker text]" + marker);
		}

		public MarkerSetHolder getMarkerSetHolder() {
			return markerSetHolder;
		}

		public void setMarkerSetHolder(MarkerSetHolder markerSetHolder) {
			this.markerSetHolder = markerSetHolder;
		}

		public MarkerSet getMarkerSet() {
			return markerSet;
		}

		public void setMarkerSet(MarkerSet markerSet) {
			this.markerSet = markerSet;
		}

		public Marker getMarker() {
			return marker;
		}

		public void setMarker(Marker marker) {
			this.marker = marker;
		}

	}

	public String regexp(String pattern, String msg) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(msg);
		if (!m.find() || m.groupCount() != 1) {
			return null;
		}
		return m.group(1);

	}
}
