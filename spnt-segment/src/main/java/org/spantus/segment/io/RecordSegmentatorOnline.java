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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.spantus.core.io.RecordWraperExtractorReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.core.wav.AudioManagerFactory;
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
		try{
			
                        AudioInputStream ais = 
                                AudioManagerFactory.createAudioManager().findInputStreamInMils(
                                reader.getAudioBuffer(),
                                marker.getStart(),
                                marker.getLength(),
                                reader.getFormat());
                        getWords().getMarkers().add(marker);
			return saveSegmentAccepted(marker, ais, stringToFile(marker.getLabel()));
		}catch (IndexOutOfBoundsException e) {
			log.error(MessageFormat.format("buffer: {3}.marker: {0}. samples: [{1};{2}]", 
					marker.toString(),marker.getStart(), 
                                        marker.getEnd(), reader.getAudioBuffer().size()));
			log.error(e);
		}
		return null;
	}

        
	/**
         * 
         * @param ais
         * @param file
         * @return
         */
        protected URL saveSegmentAccepted(Marker marker, AudioInputStream ais, File file) {
            FileUtils.checkDirs(file.getParent());
            try {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
                log.debug("[saveSegmentAccepted] {0} saved {1}", marker, path);
                return file.toURI().toURL();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        /**
         * 
         * @param name
         * @return
         */
        public URL saveFullSignal(String name) {
            Assert.isTrue(StringUtils.hasText(name), "Name cannot be empty");
            return saveFullSignal(stringToFile(name));
        }
	
       
       /**
        * 
        * @param file
        * @return
        */
        public URL saveFullSignal(File wavFile) {
            if (wavFile == null) {
                return null;
            }
            byte audioData[] = reader.getAudioBuffer().toByteArray();
            InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioInputStream ais =
                    new AudioInputStream(byteArrayInputStream,
                    reader.getFormat(),
                    audioData.length / reader.getFormat().
                    getFrameSize());
            return saveSegmentAccepted(null, ais, wavFile);
        }
	
	@Override
	protected Marker createMarker(SegmentEvent event) {
		Marker marker = super.createMarker(event);
		marker.getExtractionData().setStartSampleNum(event.getSample());
		return marker;
	}
	
	@Override
	protected Marker finazlizeMarker(Marker marker, SegmentEvent event) {
		Marker rtnMarker = super.finazlizeMarker(marker, event);
		if (rtnMarker == null)
			return rtnMarker;
		rtnMarker.getExtractionData().setEndSampleNum(event.getSample());
		return marker;
	}
	
	 protected File stringToFile(String name){
            File wavFile = null;
            if (path != null && !"".equals(path)) {
                String pathToWav = getPath() + "/" + name + ".wav";
                FileUtils.checkDirs(getPath());
                wavFile = new File(pathToWav);
            }
            return wavFile;
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
