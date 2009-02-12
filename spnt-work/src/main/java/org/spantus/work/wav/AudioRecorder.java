package org.spantus.work.wav;

/**
 * Author: Arunkumar Subramaniam
 * Owner : SFRC IT Solutions Pvt Ldt
 * 
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.sound.sampled.*;

public class AudioRecorder extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean stopCapture = false;
	ByteArrayOutputStream byteArrayOutputStream;
	AudioFormat audioFormat;
	TargetDataLine targetDataLine;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	File file = new File("arun.au");
	FileOutputStream fout;
	AudioFileFormat.Type fileType;

	public static void main(String args[]) {
		new AudioRecorder();
	}// end main

	public AudioRecorder() {// constructor
		try {
			fout = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		}
		final JButton captureBtn = new JButton("Capture");
		final JButton stopBtn = new JButton("Stop");
		final JButton playBtn = new JButton("Save");

		captureBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		playBtn.setEnabled(false);

		captureBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				captureBtn.setEnabled(false);
				stopBtn.setEnabled(true);
				playBtn.setEnabled(false);

				captureAudio();
			}
		});
		getContentPane().add(captureBtn);

		stopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				captureBtn.setEnabled(true);
				stopBtn.setEnabled(false);
				playBtn.setEnabled(true);
				// Terminate the capturing of
				// input data from the
				// microphone.
				stopCapture = true;
			}// end actionPerformed
		}// end ActionListener
				);// end addActionListener()
		getContentPane().add(stopBtn);

		playBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Play back all of the data
				// that was saved during
				// capture.
				saveAudio();
			}// end actionPerformed
		}// end ActionListener
				);// end addActionListener()
		getContentPane().add(playBtn);

		getContentPane().setLayout(new FlowLayout());
		setTitle("Capture/Playback Demo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(250, 70);
		setVisible(true);
	}// end constructor

	// This method captures audio input
	// from a microphone and saves it in
	// a ByteArrayOutputStream object.
	private void captureAudio() {
		try {
			// Get everything set up for
			// capture
			audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(
					TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();

			// Create a thread to capture the
			// microphone data and start it
			// running. It will run until
			// the Stop button is clicked.
			Thread captureThread = new Thread(new CaptureThread());
			captureThread.start();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}// end catch
	}// end captureAudio method

	// This method plays back the audio
	// data that has been saved in the
	// ByteArrayOutputStream
	private void saveAudio() {
		try {
			// Get everything set up for
			// playback.
			// Get the previously-saved data
			// into a byte array object.
			byte audioData[] = byteArrayOutputStream.toByteArray();
			// Get an input stream on the
			// byte array containing the data
			InputStream byteArrayInputStream = new ByteArrayInputStream(
					audioData);
			AudioFormat audioFormat = getAudioFormat();
			audioInputStream = new AudioInputStream(byteArrayInputStream,
					audioFormat, audioData.length / audioFormat.getFrameSize());
			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();

			// Create a thread to play back
			// the data and start it
			// running. It will run until
			// all the data has been played
			// back.
			Thread saveThread = new Thread(new SaveThread());
			saveThread.start();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}// end catch
	}// end playAudio

	// This method creates and returns an
	// AudioFormat object for a given set
	// of format parameters. If these
	// parameters don't work well for
	// you, try some of the other
	// allowable parameter values, which
	// are shown in comments following
	// the declarations.
	private AudioFormat getAudioFormat() {
		float sampleRate = 8000.0F;
		// 8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		// 8,16
		int channels = 1;
		// 1,2
		boolean signed = true;
		// true,false
		boolean bigEndian = false;
		// true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}// end getAudioFormat
	// ===================================//

	// Inner class to capture data from
	// microphone
	class CaptureThread extends Thread {
		// An arbitrary-size temporary holding
		// buffer
		byte tempBuffer[] = new byte[10000];

		public void run() {
			byteArrayOutputStream = new ByteArrayOutputStream();
			stopCapture = false;
			try {// Loop until stopCapture is set
				// by another thread that
				// services the Stop button.
				while (!stopCapture) {
					// Read data from the internal
					// buffer of the data line.
					int cnt = targetDataLine.read(tempBuffer, 0,
							tempBuffer.length);
					if (cnt > 0) {
						// Save data in output stream
						// object.
						byteArrayOutputStream.write(tempBuffer, 0, cnt);

					}// end if
				}// end while
				byteArrayOutputStream.close();
			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}// end catch
		}// end run
	}// end inner class CaptureThread
	// ===================================//
	// Inner class to play back the data
	// that was saved.

	class SaveThread extends Thread {
		byte tempBuffer[] = new byte[10000];

		public void run() {
			try {
//				int cnt;
				// Keep looping until the input
				// read method returns -1 for
				// empty stream.

				if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.AU,
						audioInputStream)) {
					AudioSystem.write(audioInputStream,
							AudioFileFormat.Type.AU, file);
				}

			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}// end catch
		}// end run
	}// end inner class PlayThread
	// ===================================//

}// end outer class AudioCapture01.java
