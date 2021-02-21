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
package org.spantus.core.extractor;

import org.spantus.core.threshold.ClassifierEnum;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public class DefaultExtractorConfig implements IExtractorConfig{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int bufferSize;
	
	private int frameSize;

	private int windowSize;
	
	private int windowOverlap;
	
	private Double sampleRate;
	
//	private int bitsPerSample;
	
	private String windowing;
	
	private Set<String> extractors;
	
	private Map<String, ExtractorParam> parameters;

	private String preemphasis;


	private ClassifierEnum classifier;

	public DefaultExtractorConfig() {
		sampleRate = 44000D;
		windowSize = 512;
		windowOverlap = windowSize - (windowSize / 10); 
		frameSize = (windowSize * 10)+windowOverlap;
		bufferSize = 850;
	}
	
	public DefaultExtractorConfig(IExtractorConfig cloning) {
		this.sampleRate = cloning.getSampleRate();
		this.windowSize = cloning.getWindowSize();
		this.windowOverlap = cloning.getWindowOverlap();
		this.frameSize = cloning.getFrameSize();
		this.bufferSize = cloning.getBufferSize();
		this.extractors = cloning.getExtractors();
		this.parameters = cloning.getParameters();
		this.windowing = cloning.getWindowing();
//		this.bitsPerSample = cloning.getBitsPerSample();
	}
	
	
	public Double getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(Double sampleRate) {
		this.sampleRate = sampleRate;
	}

//	public int getBitsPerSample() {
//		return bitsPerSample;
//	}

//	public void setBitsPerSample(int bitsPerSample) {
//		this.bitsPerSample = bitsPerSample;
//	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(int frameSize) {
		this.frameSize = frameSize;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	public int getWindowOverlap() {
		return windowOverlap;
	}

	public void setWindowOverlap(int windowOverlap) {
		this.windowOverlap = windowOverlap;
	}

	
	public Set<String> getExtractors() {
		if(extractors == null){
			extractors = new HashSet<String>();
		}
		return extractors;
	}

	public Map<String, ExtractorParam> getParameters() {
		if(parameters == null){
			parameters = new HashMap<String, ExtractorParam>();
		}
		return parameters;
	}

	public String getWindowing() {
		return windowing;
	}

	public void setWindowing(String windowing) {
		this.windowing = windowing;
	}
	public String getPreemphasis() {
		return preemphasis;
	}
	public void setPreemphasis(String preemphasis) {
		this.preemphasis = preemphasis;
	}

	@Override
	public ClassifierEnum getClassifier() {
		return this.classifier;
	}

	public void setClassifier(ClassifierEnum classifier) {
		this.classifier = classifier;
	}


	@Override
	public String toString() {
		String msg = MessageFormat.format("{0} [sampleRate: {1}, windowSize: {2}, windowOverlap: {3}, frameSize: {4}]",
				getClass().getSimpleName(),
				sampleRate, windowSize, windowOverlap, frameSize);
		return msg;
	}
	
}
