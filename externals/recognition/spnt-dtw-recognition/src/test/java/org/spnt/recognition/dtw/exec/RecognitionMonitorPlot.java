package org.spnt.recognition.dtw.exec;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.segment.io.RecordWraperExtractorReader;
import org.spantus.segment.online.DecistionSegmentatorOnline;
import org.spantus.segment.online.MultipleSegmentatorOnline;
import org.spantus.work.ui.AbstractSegmentPlot;
import org.spantus.work.ui.SegmentMonitorPlot;
import org.spnt.recognition.segment.RecordRecognitionSegmentatorOnline;

public class RecognitionMonitorPlot extends SegmentMonitorPlot {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	protected DecistionSegmentatorOnline getSegmentatorRecordable(ExtractorParam param){
		return super.getSegmentatorRecordable(param);
	}
	@Override
	protected MultipleSegmentatorOnline getSegmentatorRecordable(){
		RecordRecognitionSegmentatorOnline multipleSegmentator = new RecordRecognitionSegmentatorOnline();
//		multipleSegmentator.setCorpusMatchListener(new WritableCorpusMatchListener());
		multipleSegmentator.setParam(createParam());
		multipleSegmentator.setReader((RecordWraperExtractorReader)getWraperExtractorReader());
		return multipleSegmentator;
	}

	public static void main(String[] args) {
		AbstractSegmentPlot monitorPlot = new RecognitionMonitorPlot();
		monitorPlot.showChart();
	}

}
