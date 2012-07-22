/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.core.test;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorVector;

/**
 *
 * @author mondhs
 */
public class DummyExtractorVector implements IExtractorVector {
    private String name;
    private FrameVectorValues outputValues;


    public FrameVectorValues getOutputValues() {
        return outputValues;
    }
    public void setOutputValues(FrameVectorValues outputValues) {
        this.outputValues = outputValues;
    }

    public FrameVectorValues calculateWindow(Long sample, FrameValues frame) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public FrameVectorValues calculateWindow(FrameValues window) {
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

    @Override
    public String getRegistryName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ExtractorParam getParam() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setParam(ExtractorParam param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
