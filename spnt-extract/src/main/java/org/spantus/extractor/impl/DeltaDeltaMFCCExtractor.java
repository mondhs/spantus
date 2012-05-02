package org.spantus.extractor.impl;

import java.util.List;
import java.util.ListIterator;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.extractor.AbstractExtractorVector;

public class DeltaDeltaMFCCExtractor extends DeltaMFCCExtractor{
	
	private AbstractExtractorVector abstractExtractorVector;
	
    private List<Double> previousDeltaValues;

	public String getName() {
        return ExtractorEnum.DELTA_DELTA_MFCC_EXTRACTOR.name();
    }
	
	
	
	
	
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues deltaMfccValues = super.calculateWindow(window);
		FrameVectorValues values = new FrameVectorValues();
		values.setSampleRate(deltaMfccValues.getSampleRate());

		for (List<Double> currentList : deltaMfccValues) {
			FrameValues cachedPrevious = new FrameValues(currentList);
			FrameValues calcCurrentList = new FrameValues(currentList);
			if(previousDeltaValues == null){
				previousDeltaValues =cachedPrevious;
			}
			ListIterator<Double> iterator = calcCurrentList.listIterator();
			for (Double previous : previousDeltaValues) {
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
			previousDeltaValues = cachedPrevious;
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
