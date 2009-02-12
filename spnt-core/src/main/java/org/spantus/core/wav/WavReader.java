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
package org.spantus.core.wav;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 * 
 * @deprecated it should be used Audio Factory Instead
 */
@SuppressWarnings("dep-ann")
public class WavReader {
	static Logger log = Logger.getLogger(WavReader.class);

	URL fileURL;

	int samplingFrq = 0;

	int bytesPerSample = 0;

	DataInputStream data;

	byte[] fact = new byte[4];

	String chunk = "";

	int skip;

	boolean ch = false;

	short channel, bsize;

	byte[] rbit = new byte[2];

	boolean eof = false;

	// private AudioFormat format = null;

	public WavReader(URL fileURL) throws UnsupportedAudioFileException,
			IOException {
		log.debug("[WavReader]++++");
		this.fileURL = fileURL;
		// AudioInputStream stream;
		// stream = AudioSystem.getAudioInputStream(fileURL);
		log.debug("[WavReader]----");
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException {
		try {

			data = new DataInputStream(new BufferedInputStream(fileURL
					.openStream()));
		} catch (FileNotFoundException e) {
			log.error("File not found! ");
			return;
		} catch (IOException ex) {
			log.error("[readData] IO error.");
			return;
		}

		data.skip(12);
		data.read(fact); // type check
		chunk = new String(fact);
		data.read(fact);
		skip = WavUtils.btoi(fact);
		if (!chunk.equals("fmt ")) { // 76 stereo
			data.skip(skip + 8);
			ch = true;
		}
		data.skip(2);
		data.read(rbit); // channel
		channel = WavUtils.btos(rbit);
		if (channel == 2)
			ch = true;
		log.debug("[readData] channel " + channel);
		data.read(fact); // samplingFrq
		samplingFrq = WavUtils.btoi(fact);
		log.debug("[readData] channel " + samplingFrq);
		data.skip(6);
		data.read(rbit); // bits
		bsize = WavUtils.btos(rbit);
		bytesPerSample = bsize / 8;
		if (skip == 18) // 58 makewave
			data.skip(2);
		data.read(fact);
		chunk = new String(fact);
		if (chunk.equals("fact")) { // 58 makewave
			data.skip(12);
			// lavel = "<fact>";
		} else if (!chunk.equals("data")) {
			log.error("<" + chunk + "> ?");
			return;
		}
		if (bsize != 16) {
			log.error("Sorry. 16bit only.");
			return;
		}
		log.debug("[readData] bsize " + bsize);
		data.read(fact); // max data
		int readSamplesSize = WavUtils.btoi(fact);
		if (ch || bytesPerSample == 2)
			readSamplesSize = readSamplesSize >> 1; // 1/2
		log.debug("[readData] readSamplesSize " + (readSamplesSize/2));
//		readSamples = new float[readSamplesSize / 2]; // max wave

	}

	public boolean hasNext() {
		return eof;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public float readFloat() {

		try {
			float readSample;
			if (ch) { // stereo
				int left = WavUtils.bgtolt(data.readShort());
				int right = WavUtils.bgtolt(data.readShort());
				readSample = (float) ((left + right) / 2 * WavUtils.MIN_POSITIVE_SHORT);
				log.debug("[] stereo:  " + readSample);
			} else { // mono

				Short readShort = new Short(data.readShort());
				float sample1 = (float) (WavUtils.bgtolt(readShort.shortValue()) * WavUtils.MIN_POSITIVE_SHORT);
				readShort = new Short(data.readShort());
				float sample2 = (float) (WavUtils.bgtolt(readShort.shortValue()) * WavUtils.MIN_POSITIVE_SHORT);

				readSample = (sample1 + sample2) / 2;
				// log.debug("[readData] " + "readSample: "
				// + readSamples[readSamplesIter-1] + "; sample1: " +
				// sample1
				// + "; sample2: " + sample2
				// );
			}
			return readSample;
		} catch (IOException e) {
			eof = true;
		}
		return 0;
	}

	public void close() {
		try {
			data.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getSamplingFrq() {
		return samplingFrq;
	}

	public void setSamplingFrq(int samplingFrq) {
		this.samplingFrq = samplingFrq;
	}
}
