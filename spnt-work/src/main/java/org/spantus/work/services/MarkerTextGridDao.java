/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
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
 * TextGrid format support
 * 
 *
 *	<br>format:<br>
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
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
//				log.debug("read "+ strLine);
				if(strLine.matches(patternItem)){
					readListener.readItem(strLine);
				}else if(strLine.matches(patternIntervals)){
					readListener.readIneterval(strLine);
				}else if(strLine.matches(patternXmin)){
					readListener.readXmin(strLine);
				}else if(strLine.matches(patternXmax)){
					readListener.readXmax(strLine);
				}else if(strLine.matches(patternText)){
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
		// TODO Auto-generated method stub
		return null;
	}

	public void write(MarkerSetHolder holder, File file) {
		// TODO Auto-generated method stub

	}

	public void write(MarkerSetHolder holder, OutputStream outputStream) {
		// TODO Auto-generated method stub

	}
	
	public class ReadListener{
		MarkerSetHolder markerSetHolder = new MarkerSetHolder();
		MarkerSet markerSet;
		Marker marker;
		
		public void readItem(String index){
			markerSet = new MarkerSet();
			String str = regexp(patternItem, index);
			if("1".equals(str)){
				markerSet =  new MarkerSet();
				markerSetHolder.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), markerSet);
				marker = null;
			}else if("2".equals(str)){
				markerSet =  new MarkerSet();
				markerSetHolder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), markerSet);
				marker = null;
			}

			log.debug("[markerset]"+ markerSetHolder);
			
		}
		public void readIneterval(String index){
			String str = regexp(patternIntervals, index);
			marker = new Marker();
			markerSet.getMarkers().add(marker);
			marker.setLabel(str);
			log.debug("[marker]"+markerSet);
			
		}
		public void readXmin(String time){
			String str = regexp(patternXmin, time);
			Float start = Float.valueOf(str)*1000;
			if(marker != null){
				marker.setStart(start.longValue());
			}
			log.debug("[marker start]"+marker);
		}
		public void readXmax(String time){
			String str = regexp(patternXmax, time);
			Float end = Float.valueOf(str)*1000;
			if(marker != null){
				marker.setEnd(end.longValue());
			}
			log.debug("[marker end]"+marker);
			
		}
		public void readText(String text){
			String str = regexp(patternText, text);
			str = str.replace("\"", "");
			if(marker != null){
				if("".equals(str.trim())){
					markerSet.getMarkers().remove(marker);
				}
				marker.setLabel(str);
			}
			log.debug("[marker text]"+marker);
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
