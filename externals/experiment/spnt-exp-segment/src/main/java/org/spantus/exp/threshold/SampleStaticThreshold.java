package org.spantus.exp.threshold;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.threshold.StaticThreshold;

public class SampleStaticThreshold extends StaticThreshold{
	
	@Override
	public void setExtractor(IExtractor extractor) {
		super.setExtractor(extractor);
		afterCalculated(0L, extractor.getOutputValues());
	}

}
