package org.spantus.demo.audio;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.logger.Logger;

public class DemoAudioManager implements AudioManager {

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
	
	private void play(AudioInputStream stream, float from, float length) {
		log.debug("[play] from: " + from +"; length=" + length);
//		log.debug("[play] loaded: " + stream.getFormat());
		double totalTime = getTotalTime(stream);
		long size = stream.getFrameLength();
		double byteTimeRate = totalTime / size;
		double ends = from + length;
		if (from > totalTime || ends > totalTime) {
			// log.error("[play(float, float)] time marks are not valid! starts:
			// "
			// + starts + "; length: " + length + "; total time: "
			// + totalTime);
			return;
		}
		long startsBytes = (long) ((from * 2) / byteTimeRate);

		long endBytes = (long) ((ends * 2) / byteTimeRate);
		long lengthBytes = endBytes - startsBytes;
		// log.debug("[play(float, float)] starts bytes: " + startsBytes
		// + "; length bytes: " + lengthBytes + "; byteTimeRate: "
		// + byteTimeRate + "; totalTime: " + totalTime);
		// if (!playing) {
		// playing = true;
		// prepare();
		Playback pl = new Playback(stream, startsBytes, lengthBytes);
		pl.start();
		// } else {
		// log.error("[play(float, float)] already is palaying");
		// }
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

				while ((skiped = stream.skip(readSize)) > 0
						&& totalByte < starts && isPlaying()) {

					totalByte += skiped;
					if ((starts - (totalByte + buffer.length)) < buffer.length) {
						readSize = starts - totalByte;
					}
				}
				totalByte = 0;
				readSize = Math.min(length, buffer.length);
				setPlaying(true);
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
