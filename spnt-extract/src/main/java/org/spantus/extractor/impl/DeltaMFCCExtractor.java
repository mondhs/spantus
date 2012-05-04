package org.spantus.extractor.impl;

import java.util.List;
import java.util.ListIterator;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.extractor.AbstractExtractorVector;

public class DeltaMFCCExtractor extends AbstractSpectralVectorExtractor{
	
	private AbstractExtractorVector abstractExtractorVector;
	
    private List<Double> previousValues;

	public String getName() {
        return ExtractorEnum.DELTA_MFCC_EXTRACTOR.name();
    }
	
	private void syncParams(){
		getAbstractExtractorVector().setConfig(getConfig());
	}
	
	protected FrameVectorValues calculateMFCC(FrameValues window){
		syncParams();
		return getAbstractExtractorVector().calculateWindow(window);
	}
	
	
	
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues mfccValues = calculateMFCC(window);
		FrameVectorValues values = new FrameVectorValues();
		values.setSampleRate(mfccValues.getSampleRate());

		for (List<Double> currentList : mfccValues) {
			FrameValues cachedPrevious = new FrameValues(currentList, 1D);
			FrameValues calcCurrentList = new FrameValues(currentList, 1D);
			if(previousValues == null){
				previousValues =cachedPrevious;
			}
			ListIterator<Double> iterator = calcCurrentList.listIterator();
			for (Double previous : previousValues) {
				Double current = iterator.next();
				double delta = current - previous;
//				delta = 10D*Math.log10(Math.pow((delta),2));
//				if(Double.isInfinite(delta)){
//					delta = 0;
//				}
				values.updateMinMax(delta);
				iterator.set(delta);
			}
			values.add(calcCurrentList);
			
			previousValues = cachedPrevious;
		}

		return values ;
	}

	public AbstractExtractorVector getAbstractExtractorVector() {
		if(abstractExtractorVector == null){
			abstractExtractorVector = ExtractorUtils.createMFCCExtractor();
		}
		return abstractExtractorVector;
	}


	public void setAbstractExtractorVector(
			AbstractExtractorVector abstractExtractorVector) {
		this.abstractExtractorVector = abstractExtractorVector;
	}

}
