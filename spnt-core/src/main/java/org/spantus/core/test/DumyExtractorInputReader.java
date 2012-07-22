package org.spantus.core.test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
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
	Set<IExtractor> extractorRegister = new HashSet<IExtractor>();
	Set<IExtractorVector> extractorRegisterVector = new HashSet<IExtractorVector>();
	
	
	
	public Set<IExtractor> getExtractorRegister() {
		return extractorRegister;
	}

	
	public Set<IExtractorVector> getExtractorRegister3D() {
		
		return extractorRegisterVector;
	}

        public Set<IGeneralExtractor<?>> getGeneralExtractor() {
        	Set<IGeneralExtractor<?>> general  =new HashSet<IGeneralExtractor<?>>();
        	general.addAll(getExtractorRegister());
        	general.addAll(getExtractorRegister3D());
            return general;
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

	
	public void registerExtractor(IGeneralExtractor<?> extractor) {
		
	}

	
	public void setConfig(IExtractorConfig config) {
		
	}


	public IExtractorConfig getConfig() {
		return new DefaultExtractorConfig();
	}


	public LinkedList<Double> getWindow() {
		return window;
	}
	
    public static DummyExtractorVector createExtractorVector(String extractorName) {
        DummyExtractorVector extractor = new DummyExtractorVector();
        extractor.setName("BUFFERED_" +extractorName);
        FrameVectorValues fullFVV = generateOutputValues(10,3);
        extractor.setOutputValues(fullFVV);
        return extractor;
    }
    
    public static DummyExtractor createExtractor(String extractorName) {
        DummyExtractor extractor = new DummyExtractor();
        extractor.setName("BUFFERED_" +extractorName);
        FrameValues fullFVV = generateOutputValues(100);
        extractor.setOutputValues(fullFVV);
        return extractor;
    }
    
    public static FrameVectorValues generateOutputValues(int index, int depth){
        FrameVectorValues fullFVV = new FrameVectorValues();
        fullFVV.setSampleRate(1000D);

        for (int i = 0; i < index; i++) {
        	Double f = i+.1D;
            fullFVV.add(new Double[]{f, f, f});
        }
        return fullFVV;
    }
    public static FrameValues generateOutputValues(int index){
    	FrameValues fullF = new FrameValues();
        fullF.setSampleRate(1000D);

        for (int i = 0; i < index; i++) {
        	Double f = i+.1D;
            fullF.add(f);
        }
        return fullF;
    }

    @Override
    public Long getAvailableStartMs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long getAvailableSignalLengthMs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FrameValues findSignalValues(Long startMs, Long lengthMs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   

}
