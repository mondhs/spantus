package org.spantus.work.services;

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
/**
 * Htc laba format support
 * @author mgreibus
 *
 */
public class MarkerLabaDao implements MarkerDao {

	Logger log = Logger.getLogger(MarkerLabaDao.class);
	
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
			Marker m = null;
			Double lastEnd = null;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				String[] entries = strLine.split("\\s");
				if(entries.length == 4){
					Double start = Double.valueOf(entries[0])/1E4;
					Double end = Double.valueOf(entries[1])/1E4;
//					Double length = end - start;
					if(m!=null){
						if(lastEnd != null){
							m.setEnd(lastEnd.longValue());
						}
						markerSet.getMarkers().add(m);
						log.debug("read "+ m);
						m = null;
						
					}
					m = new Marker();
					m.setStart(start.longValue());
					m.setLabel(entries[3]);
					lastEnd = end;
					
				}else if(entries.length == 3){
					Double end = Double.valueOf(entries[1])/1E4;
					lastEnd = end;
				}
			}
			if(lastEnd != null){
				m.setEnd(lastEnd.longValue());
				markerSet.getMarkers().add(m);
				m = null;
				log.debug("read "+ m);
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
