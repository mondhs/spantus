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
package org.spantus.segment.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.logger.Logger;
import org.spantus.segment.online.DecistionSegmentatorOnline;

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

public class RecordSegmentatorOnline extends DecistionSegmentatorOnline {
	private Logger log = Logger.getLogger(RecordSegmentatorOnline.class);
	
	RecordWraperExtractorReader reader;
	
	MarkerSet words;
	
	String path;
	
	@Override
	protected boolean onSegmentEnded(Marker marker) {
		if(!super.onSegmentEnded(marker)) return false;
		processAcceptedSegment(marker);
		return true;
	}
	/**
	 * 
	 * @param marker
	 * @return
	 */
	public URL processAcceptedSegment(Marker marker){
		int bytesPerSample = (reader.getFormat().getSampleSizeInBits()>>3);// 16bit==2; 8bit==1

		long offset = (-reader.getOffset()); 
		int fromIndex =(int)(offset + marker.getExtractionData().getStartSampleNum())*bytesPerSample;
		int toIndex = fromIndex + (marker.getExtractionData().getLengthSampleNum().intValue()*bytesPerSample);
		
		try{
			getWords().getMarkers().add(marker);
			List<Byte> data = reader.getAudioBuffer().subList(fromIndex, toIndex);
			return saveSegmentAccepted(data, marker.getLabel());
		}catch (IndexOutOfBoundsException e) {
			log.error(MessageFormat.format("buffer: {3}.marker: {0}. samples: [{1};{2}]", 
					marker.toString(),fromIndex, toIndex, reader.getAudioBuffer().size()));
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param data
	 * @param name
	 * @return
	 */
	public URL saveSegmentAccepted(List<Byte> data, String name){
		if(path!= null && !"".equals(path)){
			String path = getPath()+"/"+name+".wav";
    		File wavFile = new File(path);
    		return saveSegmentAccepted(data, wavFile);
		}
		return null;
	}
	
	public URL saveSegmentAccepted(List<Byte> data, File file){
	    InputStream bais = new ByteListInputStream(data);
	    AudioInputStream ais = new AudioInputStream(bais, reader.getFormat(), data.size());
	    try {
	    	AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
	    	log.debug("[saveSegmentAccepted] saved{0}", path);
	    	return file.toURI().toURL();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public URL saveFullSignal(String name){
		return saveSegmentAccepted(reader.getAudioBuffer(), name);
	}
	
	public URL saveFullSignal(File file){
		return saveSegmentAccepted(reader.getAudioBuffer(), file);
	}
	
	@Override
	protected Marker createSegment(Long sample,
			Long time) {
		Marker marker = super.createSegment(sample, time);
		marker.getExtractionData().setStartSampleNum(sample);
		return marker;
	}
	
	@Override
	protected Marker finazlizeSegment(Marker marker, Long sample, 
			Long time) {
		Marker rtnMarker = super.finazlizeSegment(marker, sample, time);
		if (rtnMarker == null)
			return rtnMarker;
		rtnMarker.getExtractionData().setEndSampleNum(sample);
		return marker;
	}
	
	
	public void setReader(RecordWraperExtractorReader reader) {
		this.reader = reader;
	}

	public RecordWraperExtractorReader getReader() {
		return reader;
	}

	public String getPath() {
		if(path == null){
			path = "./target/";
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public MarkerSet getWords() {
		if(words == null){
			words = new MarkerSet();
		}
		return words;
	}

	public void setWords(MarkerSet words) {
		this.words = words;
	}
}
