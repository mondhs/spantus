package org.spantus.work.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.sampled.AudioFileFormat;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.io.AbstractAudioReader;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.exception.ProcessingException;
import org.spantus.mpeg7.io.Mpeg7Factory;
import org.spantus.work.WorkReadersEnum;
import org.spantus.work.reader.MultiFeatureExtractorInputReader;

public class WorkAudioReader extends AbstractAudioReader{
	AudioReader workReader;
	AudioReader mpeg7Reader;
	
	public WorkAudioReader(WorkReadersEnum readerType) {
		switch (readerType) {
		case simple:
			workReader = AudioFactory.createAudioReader();
			break;
		case multiFeature:
			workReader = AudioFactory.createAudioReader();
			if(workReader instanceof ProcessedFrameLinstener){
				((ProcessedFrameLinstener)workReader).registerProcessedFrameLinstener(this);
			}
			mpeg7Reader = Mpeg7Factory.createAudioReader();
			break;
		default:
			throw new RuntimeException("Not implemented: " + readerType);
		}
	}

	public AudioFileFormat getAudioFormat(URL url) {
		return workReader.getAudioFormat(url);
	}

	
	public void readAudio(URL url, IExtractorInputReader extractor)
			throws ProcessingException {
		if(extractor instanceof MultiFeatureExtractorInputReader){
			MultiFeatureExtractorInputReader mf = (MultiFeatureExtractorInputReader)extractor;
			mf.getMpeg7Reader().getConfig().setSampleRate(getAudioFormat(url).getFormat().getSampleRate());
			mpeg7Reader.readAudio(url, mf.getMpeg7Reader());
			workReader.readAudio(url, mf.getDefaultReader());
		}else{
			workReader.readAudio(url, extractor);	
		}
		postProcess(extractor);
	}
	
	protected void postProcess(IExtractorInputReader reader){
		for (IExtractor extractor : reader.getExtractorRegister()) {
			if(isInverted(extractor)){
				FrameValues vals = invert(extractor.getOutputValues());
				extractor.getOutputValues().clear();
				extractor.getOutputValues().addAll(vals);
				
			}
		}
		for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
			if(isFliped(extractor)){
				FrameVectorValues vals = flip(extractor.getOutputValues());
				extractor.getOutputValues().clear();
				extractor.getOutputValues().addAll(vals);
			}
		}
	}
	public FrameVectorValues flip(FrameVectorValues values){
		FrameVectorValues rtnVal = new FrameVectorValues(values);
		for (List<Float> vals : values) {
			Collections.reverse(vals);	
		}
		return rtnVal;
		
	}	
	public FrameValues invert(FrameValues values){
		Float min = Float.MAX_VALUE, max=-Float.MAX_VALUE;
		for (Float float1 : values) {
			min = Math.min(float1, min);
			max = Math.max(float1, max);
		}
		Float delta = max - min;
		FrameValues rtnVal = new FrameValues();
		for (Float float1 : values) {
			Float _f = (float1 - min)/delta;
			rtnVal.add(1-_f);	
		}
		return rtnVal;
	}
	protected boolean isFliped(IGeneralExtractor extractor){
		if(!(extractor instanceof IExtractorVector)){
			return false;
		}
		String[] exts = new String[]{
				"AudioSpectrumEnvelopeType",
				"mpeg7hc:AudioSpectrumDistributionType",
				"AudioSpectrumFlatnessType"
				};
		Set<String> flipedExtractors = new HashSet<String>(Arrays.asList(exts));
		return flipedExtractors.contains(extractor.getName());
	}
	protected boolean isInverted(IExtractor extractor){
			String[] exts = new String[]{
					"BUFFERED_SMOOTHED_LOG_ATTACK_TIME",
					"AudioSpectrumSpreadType", 
					"AudioSpectrumCentroidType",
					"AudioFundamentalFrequencyType"
					};
			Set<String> invertedExtractors = new HashSet<String>(Arrays.asList(exts));
//			invertedExtractors.addAll();
		
		return invertedExtractors.contains(extractor.getName());
		
	}
}
