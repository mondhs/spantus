package org.spantus.core.extractors.test;

import java.util.LinkedList;
import java.util.Set;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.logger.Logger;

public class DumyExtractorInputReader implements IExtractorInputReader {
	
	Logger log = Logger.getLogger(getClass());
	LinkedList<Float> window = new LinkedList<Float>();
	Float lastMin=Float.MAX_VALUE, lastMax=Float.MIN_VALUE;
	int same = 0;
	Long sample = 0L;
	
	
	public Set<IExtractor> getExtractorRegister() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Set<IExtractorVector> getExtractorRegister3D() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void pushValues(Long sample) {
		log.debug("pushValues");
	}

	
	public void put(Long sample, float value) {
		window.add(value);
		if(window.size() > 10){
			window.poll();	
		}
		Float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
		for (Float f1 : window) {
			min = Math.min(f1,min);
			max = Math.max(f1,max);
		}
		if(equals(min, lastMin) && equals(max, lastMax)){
			same++;
		}else {
			log.debug(";min:" + min + ";max:" + max+ "; same for" + same);	
			same = 0;
			lastMin = min;
			lastMax = max;
		}
		
		
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
		// TODO Auto-generated method stub
		return null;
	}

}
