/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.spantus.core.extractor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.windowing.WindowBufferProcessor;
import org.spantus.core.extractor.windowing.WindowBufferProcessorCtx;
import org.spantus.logger.Logger;
import org.spantus.math.windowing.Windowing;
import org.spantus.math.windowing.WindowingEnum;
import org.spantus.math.windowing.WindowingFactory;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.04.01
 * 
 */
public class DefaultExtractorInputReader implements IExtractorInputReader {
    protected Logger log = Logger.getLogger(DefaultExtractorInputReader.class);

    private FrameValues frameValues;

    private int frameIndex;
	
    private Long offset = 0L;
    private Long availableStartIndex=0L;
    private FrameValues signalValues = null;

    private WindowBufferProcessorCtx ctx;
	
    private WindowBufferProcessor windowBufferProcessor;

    private Windowing windowing;
	
    private IExtractorConfig config;

    private Set<IExtractor> extractorRegister = new LinkedHashSet<IExtractor>();
    private Set<IExtractorVector> extractorRegister3D = new LinkedHashSet<IExtractorVector>();
    private Set<IGeneralExtractor<?>> generalExtractor = new LinkedHashSet<IGeneralExtractor<?>>();

	public DefaultExtractorInputReader() {
		initValues();
	}

	public void put(Long sample, Double value) {
		frameValues.add(frameIndex++, value);
                signalValues.add(value);
		if (frameIndex >= config.getFrameSize()) {
			pushFrameOfWindows(sample, frameValues);
			initValues();
		}
	}

	private void initValues() {
		offset += frameIndex;
		frameValues = new FrameValues();
		frameValues.setSampleRate(getConfig().getSampleRate());
		frameIndex = 0;
	}

	public void registerExtractor(IExtractor extractor) {
		extractor.setConfig(getConfig());
		extractorRegister.add(extractor);
		generalExtractor.add(extractor);
	}

	public void registerExtractor(IExtractorVector extractor) {
		extractor.setConfig(getConfig());
		extractorRegister3D.add(extractor);
		generalExtractor.add(extractor);
	}

	public void registerExtractor(IGeneralExtractor<?> extractor) {
		if (extractor instanceof IExtractor) {
			registerExtractor((IExtractor) extractor);
		} else if (extractor instanceof IExtractorVector) {
			registerExtractor((IExtractorVector) extractor);
		}

	}

	public void pushValues(Long sample) {
		pushFrameOfWindows(sample, frameValues);
		for (IGeneralExtractor<?> element : generalExtractor) {
			element.flush();
		}
	}

	
	/**
	 * 
	 * @param sample
	 * @param ivalues
	 */
	protected void pushFrameOfWindows(Long sample, FrameValues ivalues) {

		long sampleIndex = sample - ivalues.size();
		for (Double value : frameValues) {
			FrameValues window = getWindowBufferProcessor().calculate(value,
					getCtx());
			if (window != null) {
				window.setFrameIndex(sampleIndex);
				getWindowing().apply(window);
				pushWindowValues(sample, window);
			}
			sampleIndex++;
		}
	}
	
	/**
	 * 
	 * @param sample
	 * @param ivalues
	 */
	protected void pushWindowValues(Long sample, FrameValues ivalues) {
		for (IGeneralExtractor<?> element : generalExtractor) {
			element.calculateWindow(sample, ivalues);
		}
	}


	public Set<IExtractor> getExtractorRegister() {
		return extractorRegister;
	}

	public Set<IExtractorVector> getExtractorRegister3D() {
		return extractorRegister3D;
	}

	public Set<IGeneralExtractor<?>> getGeneralExtractor() {
		return generalExtractor;
	}

	public void setGeneralExtractor(Set<IGeneralExtractor<?>> generalExtractor) {
		this.generalExtractor = generalExtractor;
	}

	public IExtractorConfig getConfig() {
		if (config == null) {
			config = new DefaultExtractorConfig();
		}
		return config;
	}

	public void setConfig(IExtractorConfig config) {
		this.config = config;
                signalValues = new FrameValues(getConfig().getSampleRate());
		for (IGeneralExtractor<?> iExtr : generalExtractor) {
			iExtr.setConfig(config);
		}
	}

	public long getFullSampleIndex() {
		return getOffset() + frameIndex;
	}

	public Long getOffset() {
		return offset;
	}

	public FrameValues getFrameValues() {
		return frameValues;
	}
	
	public WindowBufferProcessorCtx getCtx() {
		if(ctx == null){
			ctx = WindowBufferProcessor.ctreateWindowBufferProcessorCtx(getConfig());
		}
		return ctx;
	}

	public void setCtx(WindowBufferProcessorCtx ctx) {
		this.ctx = ctx;
	}

	public WindowBufferProcessor getWindowBufferProcessor() {
		if(windowBufferProcessor == null){
			windowBufferProcessor = new WindowBufferProcessor();
		}
		return windowBufferProcessor;
	}

	public void setWindowBufferProcessor(WindowBufferProcessor windowBufferProcessor) {
		this.windowBufferProcessor = windowBufferProcessor;
	}
	
    public Windowing getWindowing() {
        if (windowing == null) {
            WindowingEnum wenum = WindowingEnum.Hamming;
            if (getConfig().getWindowing() != null) {
                wenum = WindowingEnum.valueOf(getConfig().getWindowing());
            }
            windowing = WindowingFactory.createWindowing(wenum);
        }
        return windowing;
    }

    @Override
    public Long getAvailableStartMs() {
        return signalValues.toTime(availableStartIndex);
    }

    @Override
    public Long getAvailableSignalLengthMs() {
        return signalValues.getTime();
    }

    @Override
    public FrameValues findSignalValues(Long startMs, Long lengthMs) {
       int fromIndex = signalValues.toIndex(startMs)+1;
       
       Long availableEndIndex = availableStartIndex+(long)signalValues.size();
       int lengthIndex = signalValues.toIndex(lengthMs);
       org.spantus.utils.Assert.isTrue(availableStartIndex<=fromIndex &&
               fromIndex<availableEndIndex, "from {0} should be beteen[{1};{2}]", fromIndex,availableStartIndex, availableEndIndex );
      
       lengthIndex = Math.min(lengthIndex, signalValues.size());
       int toIndex = fromIndex+lengthIndex;
       FrameValues fv = signalValues.subList(fromIndex, toIndex);
        for ( Double en : fv) {
           fv.updateMinMax(en);
        }
        fv.size();
       return fv;
       
    }

}
