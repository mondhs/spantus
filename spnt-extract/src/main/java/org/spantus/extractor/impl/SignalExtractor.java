/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.extractor.impl;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorInputReaderAware;
import org.spantus.core.extractor.windowing.WindowBufferProcessor;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class SignalExtractor extends AbstractExtractor implements IExtractorInputReaderAware{
	Logger log = Logger.getLogger(SignalExtractor.class);
        
        private IExtractorInputReader reader;
        FrameValues outputValues = null;
	
	public SignalExtractor() {
		getParam().setClassName(SignalExtractor.class.getSimpleName());
	}

	public SignalExtractor(ExtractorParam param) {
		setParam(param);
	}

	public String getName() {
		return ExtractorEnum.SIGNAL_EXTRACTOR.name();
	}
        
        

        @Override
        public FrameValues getOutputValues() {
            if(outputValues == null){
                outputValues=reader.findSignalValues(reader.getAvailableStartMs(), reader.getAvailableSignalLengthMs());
            }
            return outputValues;
        }

        
        
	@Override
	public FrameValues calculateWindow(Long sampleNum, FrameValues values) {
		return null;
	}
	
	
	public FrameValues calculateWindow(FrameValues window) {
		throw new RuntimeException("should not be window value calculated");
	}

	
	public Double getExtractorSampleRate() {
//                Double extractorSampleRate = (double)getConfig().getWindowSize()/
//                        (getConfig().getWindowSize()-getConfig().getWindowOverlap());
		return getConfig().getSampleRate();//*extractorSampleRate;
	}


    @Override
    public void setExtractorInputReader(IExtractorInputReader reader) {
        this.reader = reader;
    }

}
