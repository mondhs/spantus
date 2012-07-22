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
package org.spantus.core.threshold;

import java.util.HashSet;
import java.util.Set;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorListener;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.logger.Logger;

public abstract class AbstractClassifier implements IClassifier, IExtractorListener {
	
	private Logger log = Logger.getLogger(AbstractClassifier.class); 
	private IExtractor extractor;
	private MarkerSet markSet;
	private Marker marker;
	private Set<IClassificationListener> classificationListeners;
	private long classifierSampleNum =0l;
	private FrameValues thresholdValues;
	/**
	 * @param classifierSampleNum the classifierSampleNum to set
	 */
	public void setClassifierSampleNum(long classifierSampleNum) {
		this.classifierSampleNum = classifierSampleNum;
	}

	/**
	 * @return the classifierSampleNum
	 */
	public long getClassifierSampleNum() {
		return classifierSampleNum;
	}
	
	
	public FrameValues getThresholdValues() {
		if(thresholdValues == null){
			thresholdValues = new FrameValues();
			thresholdValues.setSampleRate(getExtractorSampleRate());
		}
		return thresholdValues;
	}

	/*
	 * (non-Javadoc)
	 * @see org.spantus.core.threshold.IClassifier#addClassificationListener(org.spantus.core.threshold.IClassificationListener)
	 */
	public boolean addClassificationListener(
			IClassificationListener classificationListener) {
		log.debug("[addClassificationListener]registering {0}",classificationListener);
		boolean result = getClassificationListeners().add(classificationListener);
		if(result){
			classificationListener.registered(getName());
		}
		return result;
	}
	/*
	 * (non-Javadoc)
	 * @see org.spantus.core.threshold.IClassifier#removeClassificationListener(org.spantus.core.threshold.IClassificationListener)
	 */
	public boolean removeClassificationListener(
			IClassificationListener classificationListener) {
		return getClassificationListeners().remove(classificationListener);
	}
	
	public IExtractor getExtractor() {
		return extractor;
	}
	
	public void setExtractor(IExtractor extractor) {
		this.extractor = extractor;
	}


	public FrameValues calculateWindow(Long sample, FrameValues values) {
		return getExtractor().calculateWindow(sample, values);
	}

	public FrameValues calculateWindow(FrameValues window) {
		throw new IllegalArgumentException("Not implemented");
	}


	public void setConfig(IExtractorConfig config) {
		getExtractor().setConfig(config);		
	}

	@Override
	public long getOffset() {
		return getExtractor().getOffset();
	}
	
	/**
	 * 
	 */
	public void beforeCalculated(Long sample, FrameValues window) {
		//do nothing
	}
	/**
	 * 
	 */
	public void flush() {
		getExtractor().flush();
	}
	
//	private Long prevSample;
	


	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + getName();
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	
	public MarkerSet getMarkSet() {
		if(markSet == null){
			markSet = new MarkerSet();
			markSet.setMarkerSetType(MarkerSetHolderEnum.phone.name());
		}
		return markSet;
	}

	public void setMarkSet(MarkerSet markerSet) {
		this.markSet = markerSet;
	}
	public FrameValues getOutputValues() {
		return getExtractor().getOutputValues();
	}
	
	public IExtractorConfig getConfig() {
		return getExtractor().getConfig();
	}

	public Double getExtractorSampleRate() {
		return getExtractor().getExtractorSampleRate();
	}

	public String getName() {
		return getExtractor().getName();
	}

	public Set<IClassificationListener> getClassificationListeners() {
		if (classificationListeners == null) {
			classificationListeners =new HashSet<IClassificationListener>();
		}
		return classificationListeners;
	}

	public void setClassificationListeners(
			Set<IClassificationListener> classificationListeners) {
		this.classificationListeners = classificationListeners;
	}
            @Override
    public String getRegistryName() {
        return getExtractor().getRegistryName();
    }

    @Override
    public ExtractorParam getParam() {
        return getExtractor().getParam();
    }

    @Override
    public void setParam(ExtractorParam param) {
        getExtractor().setParam(param);
    }
	
}
