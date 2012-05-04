package org.spantus.mpeg7.extractors.impl;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.mpeg7.extractors.AbstractMpeg7Extractor;
/**
 * Based on de.crysandt.audio.mpeg7audio.AudioPower
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.05.06
 *
 */
public class AudioPowerExtractor extends AbstractMpeg7Extractor {
	public static final String EXTRACTOR_NAME = "AudioPower";
	public static final double LOG10 = Math.log(10.0);

	
	private boolean logScale;

	@Override
	public IValues calculateWindow(Long sample, FrameValues values) {
		throw new IllegalArgumentException("Not implemented");
	}

	
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues calculatedValues = createFrameValueVector();
		double power = 0.0f;
		for (Double val : window) {
			power += Math.pow(val , 2);
		}
		power /= window.size();
		if (getLogScale())
			power = 10.0f /  LOG10 * 
				 Math.log(power + Double.MIN_VALUE) ;
		FrameValues fv = new FrameValues();
		fv.add(power);
		calculatedValues.add(fv);
		return calculatedValues;
	}

	
	public String getName() {
		return EXTRACTOR_NAME;
	}
	
	public boolean getLogScale() {
		return logScale;
	}
	
	public void setLogScale(boolean logScale) {
		this.logScale = logScale;
	}

	
	public IExtractorConfig getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Double getExtractorSampleRate() {
		// TODO Auto-generated method stub
		return 0D;
	}

	
	public void setConfig(IExtractorConfig config) {
		// TODO Auto-generated method stub
		
	}
	public long getOffset() {
		return 0;
	}

}
