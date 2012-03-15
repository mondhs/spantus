/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.work.services.test;

import java.util.HashSet;
import java.util.Set;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;

/**
 *
 * @author mondhs
 */
public class DumyExtractorInputReader implements IExtractorInputReader {
    private Set<IExtractorVector> extractorRegister3D;

    public void put(Long sample, Double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void pushValues(Long sample) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<IExtractor> getExtractorRegister() {
        return new HashSet<IExtractor>();
    }

    public Set<IExtractorVector> getExtractorRegister3D() {
        if(extractorRegister3D == null){
            extractorRegister3D = new HashSet<IExtractorVector>();
        }
        return extractorRegister3D;
    }

    public Set<IGeneralExtractor> getGeneralExtractor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public void registerExtractor(IGeneralExtractor extractor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setConfig(IExtractorConfig config) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IExtractorConfig getConfig() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    
}
