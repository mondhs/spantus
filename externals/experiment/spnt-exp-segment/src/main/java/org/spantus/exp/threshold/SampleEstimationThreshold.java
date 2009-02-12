package org.spantus.exp.threshold;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.threshold.StaticThreshold;

public class SampleEstimationThreshold extends StaticThreshold {

	@Override
	public void setExtractor(IExtractor extractor) {
		super.setExtractor(extractor);
		afterCalculated(0L, extractor.getOutputValues());
	}
	@Override
	public void afterCalculated(Long sample, FrameValues result) {
		estimateThreshold(result);
		super.afterCalculated(sample, result);
	}

	public void estimateThreshold(FrameValues result){
		//Sturges' formula, numbers of bin
		int k = log2(result.size())+1;
		Float min = Float.MAX_VALUE;
		Float max = -Float.MAX_VALUE;

		for (Float float1 : result) {
			min = Math.min(min, float1);
			max = Math.max(max, float1);
		}
		Float step = (max-min)/k;
		Float[] histogram = new Float[k+2];
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = Float.valueOf(0f);
			
		}
		
		Float histogramBin = k*.05f;
//		histogramBin = histogramBin < 1?1:histogramBin;
		Float avgThreshold = null, maxThreshold = -Float.MAX_VALUE;
		for (Float float1 : result) {
			Float i  = (float1-min)/step;
			if(histogramBin.intValue()==i.intValue()){
				if(avgThreshold == null){ avgThreshold = float1;}
				avgThreshold = (avgThreshold+float1)/2;
				maxThreshold = Math.max(maxThreshold, float1);
			}
			histogram[i.intValue()]++;
			
		}
		setCurrentThresholdValue(avgThreshold);
		
	}
	
	@Override
	protected boolean isTrained(){
		return true;
	}
	/**
	 * 
	 * @param d
	 * @return
	 */
	public static int log2(int d) {
		Double l = Math.log(d) / Math.log(2.0);
		return l.intValue();
	}

}
