/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.core.test;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;

/**
 *
 * @author mondhs
 */
public class DummyExtractor implements IExtractor{
    private String name;
    private FrameValues outputValues;


    public FrameValues getOutputValues() {
        return outputValues;
    }
    public void setOutputValues(FrameValues outputValues) {
        this.outputValues = outputValues;
    }

    public FrameValues calculateWindow(Long sample, FrameValues frame) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public FrameValues calculateWindow(FrameValues window) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void putValues(Long sample, FrameValues values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void flush() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Double getExtractorSampleRate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IExtractorConfig getConfig() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setConfig(IExtractorConfig config) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	public long getOffset() {
		return 0;
	}

}
