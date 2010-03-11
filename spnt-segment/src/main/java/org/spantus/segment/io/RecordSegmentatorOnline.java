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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.spantus.core.io.RecordWraperExtractorReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.logger.Logger;
import org.spantus.segment.online.DecisionSegmentatorOnline;
import org.spantus.utils.Assert;
import org.spantus.utils.FileUtils;
import org.spantus.utils.StringUtils;

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

public class RecordSegmentatorOnline extends DecisionSegmentatorOnline {
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
                Float sampleRate = reader.getFormat().getSampleRate();

		long offset = (-reader.getOffset()); 
//		int fromIndex =(int)(offset + marker.getExtractionData().getStartSampleNum())*bytesPerSample;
//		int toIndex = fromIndex + (marker.getExtractionData().getLengthSampleNum().intValue()*bytesPerSample);
		Float timeFrom = marker.getStart().floatValue();      
//		timeFrom -= 160;
		Float fromIndex = (timeFrom*sampleRate)/1000;
        fromIndex = (fromIndex*bytesPerSample)-offset;
		Float toIndexF = fromIndex+(marker.getLength().floatValue()*sampleRate)/1000;
//                toIndexF = toIndexF * bytesPerSample;
                Integer toIndex = toIndexF.intValue();
//                if(toIndex>values.size()){
                    toIndex = reader.getAudioBuffer().size();
//                }
                    if(fromIndex %2==0){
                        fromIndex--;
                    }

                log.error("[processAcceptedSegment] offset: " + offset + "; size:" + reader.getAudioBuffer().size());

                log.error("[processAcceptedSegment] FromIndex: " + fromIndex + "; toIndex:" + toIndex);
		try{
			getWords().getMarkers().add(marker);
			List<Byte> data = reader.getAudioBuffer().subList(fromIndex.intValue(), toIndex.intValue());
			return saveSegmentAccepted(data, marker.getLabel());
		}catch (IndexOutOfBoundsException e) {
			log.error(MessageFormat.format("buffer: {3}.marker: {0}. samples: [{1};{2}]", 
					marker.toString(),fromIndex, toIndex, reader.getAudioBuffer().size()));
			log.error(e);
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
		Assert.isTrue(StringUtils.hasText(name), "Name cannot be empty");
		if(path!= null && !"".equals(path)){
                    String path1 = getPath()+"/"+name+".wav";
                    FileUtils.checkDirs(getPath());
                    File wavFile = new File(path1);
                    return saveSegmentAccepted(data, wavFile);
                    }
		return null;
	}
	
	public URL saveSegmentAccepted(List<Byte> data, File file){
	    InputStream bais = new ByteListInputStream(data);
            FileUtils.checkDirs(file.getParent());
//            log.error(file.getParent());
            AudioInputStream ais = new AudioInputStream(bais, reader.getFormat(), data.size()/reader.getFormat().getFrameSize());
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
	protected Marker createSegment(SegmentEvent event) {
		Marker marker = super.createSegment(event);
		marker.getExtractionData().setStartSampleNum(event.getSample());
		return marker;
	}
	
	@Override
	protected Marker finazlizeSegment(Marker marker, SegmentEvent event) {
		Marker rtnMarker = super.finazlizeSegment(marker, event);
		if (rtnMarker == null)
			return rtnMarker;
		rtnMarker.getExtractionData().setEndSampleNum(event.getSample());
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
	
	
	@Override
	public String toString() {
		int wordsSize = 0;
		wordsSize = words == null || words.getMarkers() == null?0:words.getMarkers().size();
		return MessageFormat.format("{0}[path:{1}, words: {2}]", getClass().getSimpleName(), path, wordsSize);
	}
}
