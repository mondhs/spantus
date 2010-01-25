
/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
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
package org.spantus.work.ui.dto;

import org.spantus.core.extractor.preemphasis.Preemphasis.PreemphasisEnum;
import org.spantus.math.windowing.WindowingEnum;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;

/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.08.26
 *
 */
public class WorkUIExtractorConfig{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer windowSize = 30 ; //30ms
	private Integer frameSize = 10 ; //10 windows
	private Integer windowOverlap = 66; //66%
	private Integer bufferSize = 8000; //5s	
	
	private Float recordSampleRate=11025F;
	private String audioPathOutput="./";
	private Integer thresholdLeaningPeriod=5000;
	private Float thresholdCoef=2F;
	private Integer segmentationMinLength=91;
	private Integer segmentationMinSpace=61;
	private Integer segmentationExpandStart=60;
	private Integer segmentationExpandEnd=60;
	
	private String windowingType;
	private String preemphasis;
	private String segmentationServiceType;
	 
	public Integer getBufferSize() {
		return bufferSize;
	}

	 
	public Integer getFrameSize() {
		return frameSize;
	}

	 
	public Integer getWindowOverlap() {
		return windowOverlap;
	}

	 
	public Integer getWindowSize() {
		return windowSize;
	}

	 
	public void setBufferSize(Integer bufferSize) {
		this.bufferSize = bufferSize;
	}

	 
	public void setFrameSize(Integer frameSize) {
		this.frameSize = frameSize;
	}

	 
	public void setWindowOverlap(Integer windowOverlap) {
		this.windowOverlap = windowOverlap;
	}

	 
	public void setWindowSize(Integer windowSize) {
		this.windowSize = windowSize;
	}


	public Float getRecordSampleRate() {
		return recordSampleRate;
	}


	public void setRecordSampleRate(Float sampleRate) {
		this.recordSampleRate = sampleRate;
	}


	public String getAudioPathOutput() {
		return audioPathOutput;
	}


	public void setAudioPathOutput(String pathOutput) {
		this.audioPathOutput = pathOutput;
	}


	public Integer getThresholdLeaningPeriod() {
		return thresholdLeaningPeriod;
	}


	public void setThresholdLeaningPeriod(Integer leaningPeriod) {
		this.thresholdLeaningPeriod = leaningPeriod;
	}


	public Float getThresholdCoef() {
		return thresholdCoef;
	}


	public void setThresholdCoef(Float coef) {
		this.thresholdCoef = coef;
	}


	public Integer getSegmentationMinLength() {
		return segmentationMinLength;
	}


	public void setSegmentationMinLength(Integer segmentationMinLength) {
		this.segmentationMinLength = segmentationMinLength;
	}


	public Integer getSegmentationMinSpace() {
		return segmentationMinSpace;
	}


	public void setSegmentationMinSpace(Integer segmentationMinSpace) {
		this.segmentationMinSpace = segmentationMinSpace;
	}


	public Integer getSegmentationExpandStart() {
		return segmentationExpandStart;
	}


	public void setSegmentationExpandStart(Integer segmentationExpandStart) {
		this.segmentationExpandStart = segmentationExpandStart;
	}


	public Integer getSegmentationExpandEnd() {
		return segmentationExpandEnd;
	}


	public void setSegmentationExpandEnd(Integer segmentationExpandEnd) {
		this.segmentationExpandEnd = segmentationExpandEnd;
	}
	public String getWindowingType() {
		if(windowingType == null){
			windowingType = WindowingEnum.Hamming.name();
		}
		return windowingType;
	}

	public void setWindowingType(String windowingType) {
		this.windowingType = windowingType;
	}
	public String getPreemphasis(){
		if(preemphasis == null){
			preemphasis = PreemphasisEnum.full.name();
		}
		return preemphasis;
	}
	public void setPreemphasis(String preemphasis){
		this.preemphasis = preemphasis;
	}


	public String getSegmentationServiceType() {
		if(segmentationServiceType == null){
			segmentationServiceType = SegmentatorServiceEnum.offline.name();
		}
		return segmentationServiceType;
	}


	public void setSegmentationServiceType(String segmentationServiceType) {
		this.segmentationServiceType = segmentationServiceType;
	}
}
