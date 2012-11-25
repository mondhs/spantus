package org.spantus.extr.wordspot.service.scrolling.impl;

import java.io.File;

import org.spantus.core.service.CorpusService;
import org.spantus.extr.wordspot.service.impl.SegmentRecognitionThresholdServiceImpl;
import org.spantus.work.services.WorkExtractorReaderService;

public class ScrollingFactory {
	public static  WindowScrollingSpottingServiceImpl createWindowScrollingSpottingServiceImpl(File repositoryPathFile, CorpusService corpusService, 
			WorkExtractorReaderService extractorReaderService){
		WindowScrollingSpottingServiceImpl spottingService = new WindowScrollingSpottingServiceImpl();
		spottingService.setCorpusService(corpusService);
		spottingService.setExtractorReaderService(extractorReaderService);
		spottingService.setSegmentRecognitionThresholdService(new SegmentRecognitionThresholdServiceImpl(repositoryPathFile.getAbsolutePath()));
		return spottingService;
	}
}
