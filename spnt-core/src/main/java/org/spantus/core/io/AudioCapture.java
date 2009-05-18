package org.spantus.core.io;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;


public class AudioCapture extends Thread {
	
	boolean running;

	AudioFormat format;
	
	WraperExtractorReader reader;
	private Logger log = Logger.getLogger(getClass());
	
	public AudioCapture(WraperExtractorReader reader) {
		this.reader = reader;
	}
	public AudioCapture() {
	}
	
	public WraperExtractorReader getReader() {
		return reader;
	}

	public void setReader(WraperExtractorReader reader) {
		this.reader = reader;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public void setFormat(AudioFormat format) {
		reader.setFormat(format);
		this.format = format;
	}

	
	public synchronized void start() {
		super.start();
	}

	
	public void run() {
		running = true;
		final AudioFormat format = getFormat();
		reader.setFormat(format);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine line;
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();
//			log.error("grabing line. " + running + ": " + this.hashCode());
			int bufferSize = (int) format.getSampleRate()
					* format.getFrameSize();
			byte buffer[] = new byte[bufferSize];
			
			while (running) {
				line.read(buffer, 0, buffer.length);
				for (byte b : buffer) {
					reader.put(b);
				}
			}
			line.drain();
			line.close();
			line.stop();
//			log.error("line closed");
		} catch (LineUnavailableException e1) {
			log.error(e1);
			running = false;
			throw new ProcessingException(e1);
		}

	}

	public boolean isRunning(){
		return running;
	}
	
	public void finalize() {
//		log.error("finalize");
		running = false;
	}

}