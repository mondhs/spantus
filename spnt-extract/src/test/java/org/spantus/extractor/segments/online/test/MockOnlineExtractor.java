package org.spantus.extractor.segments.online.test;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.2
 * Created Mar 16, 2010
 *
 */
public class MockOnlineExtractor implements IExtractor{

	FrameValues values;
	
	public FrameValues calculate(Long sample, FrameValues values) {
		return null;
	}

	public FrameValues calculateWindow(FrameValues window) {
		return null;
	}

	public FrameValues getOutputValues() {
		if(values == null){
			values = new FrameValues();
		}
		return values;
	}

	public void flush() {
		
	}

	public IExtractorConfig getConfig() {
		return null;
	}

	public Double getExtractorSampleRate() {
		return 0D;
	}

	public String getName() {
		return null;
	}

	public void putValues(Long sample, FrameValues values) {
		
	}

	public void setConfig(IExtractorConfig config) {
	}

	@Override
	public long getOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

}
