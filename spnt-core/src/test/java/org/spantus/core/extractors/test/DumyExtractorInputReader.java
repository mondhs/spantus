package org.spantus.core.extractors.test;

import java.util.LinkedList;
import java.util.Set;

import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.logger.Logger;

public class DumyExtractorInputReader implements IExtractorInputReader {
	
	private Logger log = Logger.getLogger(getClass());
	private LinkedList<Double> window = new LinkedList<Double>();
//	private Float lastMin=Float.MAX_VALUE, lastMax=Float.MIN_VALUE;
//	private int same = 0;
//	private Long sample = 0L;
//	private Integer configedWindowSize =10;
	
	
	public Set<IExtractor> getExtractorRegister() {
		return null;
	}

	
	public Set<IExtractorVector> getExtractorRegister3D() {
		return null;
	}

        public Set<IGeneralExtractor> getGeneralExtractor() {
            return null;
        }
        
	
	public void pushValues(Long sample) {
		log.debug("pushValues");
	}

	
	public void put(Long sample, Double value) {
		window.add(value);
//		if(window.size() > configedWindowSize){
//			window.poll();	
//		}
//		Float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
//		for (Float f1 : window) {
//			min = Math.min(f1,min);
//			max = Math.max(f1,max);
//		}
//		if(equals(min, lastMin) && equals(max, lastMax)){
//			same++;
//		}else {
//			log.debug(";min:" + min + ";max:" + max+ "; same for" + same);	
//			same = 0;
//			lastMin = min;
//			lastMax = max;
//		}
//		
		
	}
	boolean equals(Float f1, Float f2){
		return Float.compare(f1, f2) == 0; 
	}

	
	public void registerExtractor(IGeneralExtractor extractor) {
		// TODO Auto-generated method stub
		
	}

	
	public void setConfig(IExtractorConfig config) {
		// TODO Auto-generated method stub
		
	}


	public IExtractorConfig getConfig() {
		return new DefaultExtractorConfig();
	}


	public LinkedList<Double> getWindow() {
		return window;
	}

}
