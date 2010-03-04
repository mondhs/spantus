
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

import java.io.File;
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
public class SpantusWorkProjectInfo {

	public enum ProjectTypeEnum{feature, segmenation, recordSegmentation, recognition};
	
	
//	private float from;
//
//	private float length;
	
	private String type;

	private WorkSample sample;
	
	private FeatureReader featureReader;
	
	private File workingDir;
	
	private String experimentId;
	
	private String classifierType;
	
//	public float getFrom() {
//		return from;
//	}
//
//	public void setFrom(float from) {
//		this.from = from;
//	}
//
//	public float getLength() {
//		return length;
//	}
//
//	public void setLength(float length) {
//		this.length = length;
//	}

	
	public WorkSample getSample() {
		if(sample == null){
			this.sample = new WorkSample();
		}
		return sample;
	}

	public void setSample(WorkSample sample) {
		this.sample = sample;
	}

	public FeatureReader getFeatureReader() {
		if(featureReader == null){
			featureReader = new FeatureReader();
		}
		return featureReader;
	}

	public void setFeatureReader(FeatureReader featureReader) {
		this.featureReader = featureReader;
	}

	public File getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(File workingDir) {
		this.workingDir = workingDir;
	}

	public String getType() {
		if(type == null){
			type = ProjectTypeEnum.segmenation.name();
		}
		return type;
	}

	public void setType(String currentType) {
		this.type = currentType;
	}

	public String getExperimentId() {
		return experimentId;
	}

	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}

	public String getClassifierType() {
//		if(thresholdType == null){
//			thresholdType = ThresholdEnum.online.name();
//		}
		return classifierType;
	}

	public void setClassifierType(String thresholdType) {
		this.classifierType = thresholdType;
	}



	
}
