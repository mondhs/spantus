package org.spantus.extractor.impl;

import java.util.List;
import java.util.ListIterator;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;

public class DeltaMFCCExtractor extends MFCCExtractor{
	
	
    private List<Double> previousValues;

	public String getName() {
        return ExtractorEnum.DELTA_MFCC_EXTRACTOR.name();
    }
	
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues mfccValues = super.calculateWindow(window);
		FrameVectorValues values = new FrameVectorValues(mfccValues);

		for (List<Double> currentList : values) {
			FrameValues cachedPrevious = new FrameValues(currentList);
			if(previousValues == null){
				previousValues =cachedPrevious;
			}
			ListIterator<Double> iterator = currentList.listIterator();
			for (Double previous : previousValues) {
				Double current = iterator.next();
				iterator.set(Math.pow((current - previous),2));
			}
			previousValues = cachedPrevious;
		}

		return values ;
	}

}
