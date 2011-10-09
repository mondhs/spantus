package org.spantus.extractor;

import java.util.Iterator;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractorConfig;

public class WindowBufferProcessor {

	public FrameValues calculate(Long sampleNum, FrameValues values,
			IExtractorConfig config, FrameValues windowValues) {
		// log.debug("[calculate]+++  name:{0}; sampleRate:{1}; windowSize:{2}",
		// getName(), getConfig().getSampleRate()/1000, getConfig()
		// .getWindowSize());
		FrameValues windowedWindow = new FrameValues(windowValues);
		long frameIndex = windowValues.getFrameIndex() == null?-1:windowValues.getFrameIndex();
		int frameIndexStartFrom = -1+(int) (config.getWindowSize() +  (frameIndex * (config.getWindowOverlap())));
		long index = 0;
		

		if(windowValues.size() == 0){
			for (Double f1 : values) {
				// i++;
				if (windowedWindow.size() < config.getWindowSize()) {
					// fill the window while it is empty
					windowedWindow.add(f1);
				}else{
					break;
				}
			}
			windowedWindow.setFrameIndex(frameIndex+1);
			return windowedWindow;
		}

		
		for (Iterator<Double> iterator = values.listIterator(frameIndexStartFrom+1); iterator
				.hasNext();) {
			Double f1 = iterator.next();
				if (index < config.getWindowOverlap()) {
					index++;
//				getWindowing().apply(windowedWindow);
				// Calculating features values for the window
//				calculatedValues.addAll(calculateWindow(windowedWindow,
//						getWindowValues()));
					windowedWindow.add(f1);
					windowedWindow.poll();
//				windowsIndex = config.getWindowSize();
			}else{
				break;
			}
		}
		if(index !=config.getWindowOverlap() ){
			return null;
		}
		windowedWindow.setFrameIndex(frameIndex+1);
		windowedWindow.setSampleRate(calculateExtractorSampleRate(config));
		// log.debug("[calculate]---");
		return windowedWindow;
	}
	/**
	 * 
	 * @param config
	 * @return
	 */
	public Double calculateExtractorSampleRate(IExtractorConfig config) {
		return (config.getSampleRate()/(config.getWindowOverlap()));
	}
}
