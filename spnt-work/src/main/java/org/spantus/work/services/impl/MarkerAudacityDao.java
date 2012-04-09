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
package org.spantus.work.services.impl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.logger.Logger;
import org.spantus.work.services.MarkerDao;
/**
 * Audacity labels format support
 *
 *	<br>format:<br>
 *	&lt;start time in sec&gt;\t&lt;end time in sec&gt;\t&lt;label&gt;\n<br>
 * 
 * @author Mindaugas Greibus
 * @since 0.0.1
 */
public class MarkerAudacityDao implements MarkerDao {

	Logger log = Logger.getLogger(MarkerAudacityDao.class);
	
	public MarkerSetHolder read(File file) {
		MarkerSetHolder markerSetHolder = new MarkerSetHolder();
		MarkerSet markerSet = new MarkerSet();
		markerSetHolder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), markerSet);
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
				String[] entries = strLine.split("\t");
				if(entries.length == 3){
					Marker m =new Marker();
					Double start = Double.valueOf(entries[0])*1E3;
					Double end = Double.valueOf(entries[1])*1E3;
					Double length = end - start;
					m.setStart(start.longValue());
					m.setLength(length.longValue());
					m.setLabel(entries[2]);
					markerSet.getMarkers().add(m);
					log.debug("read "+ m);
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			log.error(e);
		}
		return markerSetHolder;
		
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

}
