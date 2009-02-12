package org.spnt.recognition.segment;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.extractor.ExtractorInputReader;
import org.spantus.segment.online.DecistionSegmentatorOnline;

public class RecognitionSegmentatorOnline extends DecistionSegmentatorOnline {
	
	ExtractorInputReader bufferedReader;
	
	public RecognitionSegmentatorOnline(ExtractorInputReader bufferedReader){
		this.bufferedReader = bufferedReader;
	}
	@Override
	protected boolean onSegmentEnded(Marker marker) {
		if(!super.onSegmentEnded(marker)) return false; 
		bufferedReader.getOffset();
		for (IExtractor extr : bufferedReader.getExtractorRegister()) {
			extr.getOutputValues();
		}
		return true;
	}


	public IExtractorInputReader getBufferedReader() {
		return bufferedReader;
	}

}
