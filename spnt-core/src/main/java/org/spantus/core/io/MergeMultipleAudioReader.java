/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.core.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.utils.Assert;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 * Created 2008.04.11
 * 
 */
public class MergeMultipleAudioReader extends DefaultAudioReader {

	// private Logger log = Logger.getLogger(getClass());

	private URL noiseUrl;
	
	@Override
	public WraperExtractorReader createWraperExtractorReader(
			IExtractorInputReader bufferedReader) {
		return new MergedWraperExtractorReader(bufferedReader);
	}

	public void readAudioInternal(URL url)
			throws UnsupportedAudioFileException, IOException {
		Assert.isTrue(getNoiseUrl()!=null);
		AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(url);
		AudioFileFormat noiseAudioFileFormat = AudioSystem.getAudioFileFormat(getNoiseUrl());
		Assert.isTrue(audioFileFormat.getFormat().getSampleRate() ==
			noiseAudioFileFormat.getFormat().getSampleRate(), "sample rate is not the same");
		Assert.isTrue(audioFileFormat.getFormat().getSampleSizeInBits() ==
			noiseAudioFileFormat.getFormat().getSampleSizeInBits());
		Assert.isTrue(audioFileFormat.getFormat().isBigEndian() ==
			noiseAudioFileFormat.getFormat().isBigEndian());
		
		MergedWraperExtractorReader mergedWraperExtractorReader = ((MergedWraperExtractorReader)wraperExtractorReader);
		
		DataInputStream dis = new DataInputStream(new BufferedInputStream(AudioSystem.getAudioInputStream(url)));
		DataInputStream noiseDis = 
			new DataInputStream(new BufferedInputStream(AudioSystem.getAudioInputStream(getNoiseUrl())));

		Long size = Long.valueOf(audioFileFormat.getFrameLength()
				* audioFileFormat.getFormat().getFrameSize());
		started(size);
		for (long index = 0; index < size; index++) {
			int noiseByte = noiseDis.read();
			if(noiseByte ==-1){
				noiseDis.close();
				noiseDis = new DataInputStream(
					new BufferedInputStream(AudioSystem.getAudioInputStream(getNoiseUrl())));
				noiseByte = noiseDis.read();
			}
			
			mergedWraperExtractorReader.put(dis.readByte(), (byte)noiseByte);
			
			processed(Long.valueOf(index), size);
		}
		wraperExtractorReader.pushValues();
		
		log.debug("SNR: {0}", calculateSnr(mergedWraperExtractorReader.getTotalSignalEnergy(),
				mergedWraperExtractorReader.getTotalNoiseEnergy(), audioFileFormat.getFrameLength()));
		//signal/noise
	
//		mergedWraperExtractorReader.saveMerged(new File("./target/saved.wav"), audioFileFormat.getFormat());
		dis.close();
		noiseDis.close();
		ended();

	}
	protected double calculateSnr(BigDecimal totalSignalEnergy, BigDecimal totalNoiseEnergy, long size){
//		totalSignalEnergy = totalSignalEnergy.divide(BigDecimal.valueOf(size),RoundingMode.HALF_UP);
//		totalNoiseEnergy = totalNoiseEnergy.divide(BigDecimal.valueOf(size),RoundingMode.HALF_UP);
//		Double totalSignalEnergyRMS = Math.sqrt(totalSignalEnergy.doubleValue());
//		Double totalNoiseEnergyRMS = Math.sqrt(totalNoiseEnergy.doubleValue());
//		Double ratio =totalSignalEnergyRMS/totalNoiseEnergyRMS;
		BigDecimal ratio = totalSignalEnergy.divide(totalNoiseEnergy,RoundingMode.HALF_UP);
		double snr = 10*Math.log10(ratio.doubleValue());
		return snr;
	}

	public URL getNoiseUrl() {
		return noiseUrl;
	}

	public void setNoiseUrl(URL noiseUrl) {
		this.noiseUrl = noiseUrl;
	}

}
