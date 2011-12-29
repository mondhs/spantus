package org.spantus.extractor;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractorConfig;

public class WindowBufferProcessor {
	
	public static WindowBufferProcessorCtx ctreateWindowBufferProcessorCtx(IExtractorConfig config){
		WindowBufferProcessorCtx ctx = new WindowBufferProcessorCtx();
		ctx.setConfig(config);
		ctx.setBuffer(new FrameValues());
		ctx.getBuffer().setFrameIndex(0L);
		return ctx;
	}
	

	public FrameValues calculate(Double value,
			WindowBufferProcessorCtx ctx) {
		FrameValues windowedWindow = ctx.getBuffer();
		windowedWindow.add(value);
		
		if(windowedWindow.size()>=ctx.getConfig().getWindowSize()){
			windowedWindow.setSampleRate(calculateExtractorSampleRate(ctx.getConfig()));
			FrameValues newWindow =  windowedWindow.subList(ctx.getConfig().getWindowSize()-ctx.getConfig().getWindowOverlap(), windowedWindow.size());
			ctx.setBuffer(newWindow);
			ctx.getBuffer().setFrameIndex(windowedWindow.getFrameIndex()+1);			
		}else{
			return null;
		}
		
		return windowedWindow;
	}
	/**
	 * 
	 * @param config
	 * @return
	 */
	public Double calculateExtractorSampleRate(IExtractorConfig config) {
		return (config.getSampleRate()/(config.getWindowSize()-config.getWindowOverlap()));
	}
}
