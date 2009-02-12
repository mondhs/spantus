package org.spantus.core.io;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


public class AudioCapture extends Thread {
	
	boolean running;

	AudioFormat format;
	
	WraperExtractorReader reader;
	
	public AudioCapture(WraperExtractorReader reader) {
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
		running = true;
	}

	
	public void run() {
		super.run();
		final AudioFormat format = getFormat();
		reader.setFormat(format);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine line;
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();
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
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}

	}

	
	public void finalize() {
		running = false;
	}

}