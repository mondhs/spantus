package org.spantus.mpeg7.extractors.impl;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.mpeg7.extractors.AbstractMpeg7Extractor;
/**
 * Based on de.crysandt.audio.mpeg7audio.AudioWaveform
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.05.06
 *
 */
public class AudioWaveformExtractor extends AbstractMpeg7Extractor {
	public static final String EXTRACTOR_NAME = "AudioWaveform";
	/**
	 * 
	 */
	
	@Override
	public FrameVectorValues calculateWindow(Long sample, FrameValues values) {
		throw new IllegalArgumentException("Not implemented");
	}

	
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues calculatedValues = createFrameValueVector();
		Double min=null, max = null;
		for (Double val : window) {
			min = min(min, val);
			max = max(max, val);
		}
		FrameValues fv = new FrameValues();
		fv.add(min);
		fv.add(max);
		calculatedValues.add(fv);
		return calculatedValues;
	}
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	Double min(Double a, Double b){
		if(a == null && b == null){
			return null;
		}else if(a == null && b != null){
			return a;
		}else if(a != null && b == null){
			return b;
		}else{
			return Math.min(a, b);
		}
	}
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	Double max(Double a, Double b){
		if(a == null && b == null){
			return null;
		}else if(a == null && b != null){
			return a;
		}else if(a != null && b == null){
			return b;
		}else{
			return Math.max(a, b);
		}
	}

	
	public String getName() {
		return EXTRACTOR_NAME;
	}
	
	public IExtractorConfig getConfig() {
		return null;
	}
	
	public Double getExtractorSampleRate() {
		return 0D;
	}
	
	public void setConfig(IExtractorConfig config) {
		
	}
	public long getOffset() {
		return 0;
	}

	
}
