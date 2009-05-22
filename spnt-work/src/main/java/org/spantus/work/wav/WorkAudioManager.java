package org.spantus.work.wav;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;

public class WorkAudioManager implements AudioManager {

	Logger log = Logger.getLogger(getClass());

	
	public void play(URL fileURL) {
		AudioInputStream stream = createInput(fileURL);
		play(stream, 0, getTotalTime(stream));
	}


	
	public void play(URL fileURL, float from, float length) {
		AudioInputStream stream = createInput(fileURL);
		if(from == 0 && length == 0){
			length = getTotalTime(stream);
		}
		play(stream, from, length);
	}
	
	private void play(AudioInputStream stream, float starts, float length) {
		log.debug("[play] from: " + starts +"; length=" + length);
		double totalTime = getTotalTime(stream);
		double ends = starts + length;
		double adaptedLength = ends > totalTime?totalTime-starts:length;
		if (starts > totalTime) {
			log.error("[play] Cannot play due start is more than total time" + starts +">"+ totalTime);
			return;
		}
		long startsBytes = (long) ((starts * stream.getFormat().getFrameRate())*stream.getFormat().getFrameSize());
		long lengthBytes = (long) ((adaptedLength  * stream.getFormat().getFrameRate())*stream.getFormat().getFrameSize());
		Playback pl = new Playback(stream, startsBytes, lengthBytes);
		pl.start();
	}
	/**
	 * 
	 */
	public void save(URL fileURL, float starts, float length, String pathToSave) {
		log.debug("[save] from:{0}; lenght:{1}; pathToSave:{2}", starts, length, pathToSave );
		AudioInputStream stream = createInput(fileURL);
		double totalTime = getTotalTime(stream);
		double ends = starts + length;
		double adaptedLength = ends > totalTime?totalTime-starts:length;
		if (starts > totalTime) {
			log.error("[save] Cannot save due stars:"+starts+" more than total time:"+totalTime );
			return;
		}
		Long startsBytes = (long) ((starts * stream.getFormat().getFrameRate())*stream.getFormat().getFrameSize());
		Long lengthBytes = (long) ((adaptedLength  * stream.getFormat().getFrameRate())*stream.getFormat().getFrameSize());
		
		try {
			long skipedByteTotal = startsBytes;
			long skipedByte = stream.available();
			while((skipedByte=stream.skip(skipedByteTotal)) != 0 ){
				skipedByteTotal -= skipedByte;
			}
			byte[] data = new byte[lengthBytes.intValue()];
			stream.read(data);
			InputStream bais = new ByteArrayInputStream(data);
			AudioInputStream ais = new AudioInputStream(bais, stream.getFormat(), data.length);
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, FileUtils.findNextAvaibleFile(pathToSave));
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	protected float getTotalTime(AudioInputStream stream) {
		float totalTime = (stream.getFrameLength() / stream.getFormat()
				.getFrameRate());
		return totalTime;
	}

	private AudioInputStream createInput(URL fileURL)
	// Set up the audio input stream from the sound file
	{
		AudioInputStream stream = null;
		try {
			// link an audio stream to the sampled sound's file
			stream = AudioSystem.getAudioInputStream(fileURL);
			AudioFormat format = stream.getFormat();
			// log.debug("[createInput]Audio format: " + format);

			// convert ULAW/ALAW formats to PCM format
			if ((format.getEncoding() == AudioFormat.Encoding.ULAW)
					|| (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
				AudioFormat newFormat = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						format.getSampleRate(),
						format.getSampleSizeInBits() * 2, format.getChannels(),
						format.getFrameSize() * 2, format.getFrameRate(), true); // big
				// endian
				// update stream and format details
				stream = AudioSystem.getAudioInputStream(newFormat, stream);
				format = newFormat;
			}
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream;
	} // end of createInput()



	private class Playback extends Thread {

		private long starts;

		private long length;
		
		private AudioInputStream stream;

		private boolean playing;
		
//		 private static ThreadLocal playingStatus = new ThreadLocal() {
//	         protected synchronized Object initialValue() {
//	             return Boolean.valueOf(playing);
//	         }
//	     };

		
		/**
		 * 
		 */
		public Playback(AudioInputStream stream, long starts, long length) {
			this.stream = stream;
			this.starts = starts;
			this.length = length;
		}

		public AudioInputStream getStream() {
			return stream;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		
		public void run() {
			playback(stream, starts, length);
			// playing = false;
		}
		
		/**
		 * set up the SourceDataLine going to the JVM's mixer
		 * 
		 */
		private SourceDataLine createOutput(AudioFormat format) {
			SourceDataLine line = null;
			try {
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
				log.debug("[createOutput] opened output line: " + info.toString());
				if (!AudioSystem.isLineSupported(info)) {
					log.error("[createOutput]Line does not support: " + format);
				}
				// get a line of the required format
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(format);
			} catch (Exception e) {
				log.error("create output throwed exception" + e);
			}
			return line;
		}
		/**
		 * 
		 * @param stream
		 * @param starts
		 * @param length
		 */
		private void playback(AudioInputStream stream, long starts, long length) {
			SourceDataLine line = createOutput(stream.getFormat());
			byte buffer[] = new byte[line.getBufferSize()];
			line.start();
			try {

				int byteCount;
				long totalByte = 0;
				long skiped = 0;
				long readSize = Math.min(starts, buffer.length);
				setPlaying(true);
				while ((skiped = stream.skip(readSize)) > 0
						&& totalByte < starts && isPlaying()) {

					totalByte += skiped;
					if ((starts - (totalByte + buffer.length)) < buffer.length) {
						readSize = starts - totalByte;
					}
				}
				totalByte = 0;
				readSize = Math.min(length, buffer.length);
				while ((byteCount = stream.read(buffer, 0, (int) readSize)) > 0
						&& totalByte < length && isPlaying()) {

					byte[] proceedBuf = preprocessSamples(buffer, byteCount);

					if (byteCount > 0) {
						line.write(proceedBuf, 0, byteCount);
					}

					totalByte += byteCount;
					// if ((starts - (totalByte + buffer.length)) <
					// buffer.length) {
					// readSize = length - totalByte;
					// }
					readSize = Math.min((length - totalByte), readSize);
				}

				line.drain();
				line.stop();
				line.close();
			} catch (IOException e) {
				setPlaying(false);
				e.printStackTrace();
				log.error("[playback(long,long)]: " + e.getMessage());
			}

		}

		private byte[] preprocessSamples(byte[] samples, int numBytes) {
	        //		log.debug("[processSamples]++++");
	        //
	        //		short sample, newSample;
	        //		byte[] newSamples = new byte[numBytes];
	        //		for (int i = 1; i < numBytes; i++) {
	        //			newSamples[i] = (byte) ((short)samples[i]);
	        //			
	        //		}
	        //		log.debug("[processSamples]---- ");
	        //		return newSamples;
	        return samples;
	    } 
		public boolean isPlaying() {
			return playing;
		}

		public void setPlaying(boolean playing) {
			this.playing = playing;
		}

	}

}
