package org.spnt.recognition.dtw.exec;

import org.spantus.core.io.RecordWraperExtractorReader;
import org.spantus.segment.online.MultipleSegmentatorListenerOnline;
import org.spantus.work.ui.AbstractSegmentPlot;
import org.spantus.work.ui.SegmentMonitorPlot;
import org.spnt.recognition.segment.RecordRecognitionSegmentatorOnline;

public class RecognitionMonitorPlot extends SegmentMonitorPlot {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected MultipleSegmentatorListenerOnline createSegmentatorRecordable(){
		RecordRecognitionSegmentatorOnline multipleSegmentator = new RecordRecognitionSegmentatorOnline();
//		multipleSegmentator.setCorpusMatchListener(new WritableCorpusMatchListener());
		multipleSegmentator.setParam(createParam());
		multipleSegmentator.setReader((RecordWraperExtractorReader)getWraperExtractorReader());
		return multipleSegmentator;
	}

	public static void main(String[] args) {
		AbstractSegmentPlot monitorPlot = new RecognitionMonitorPlot();
		monitorPlot.showChartFrame();
		monitorPlot.startRecognition();
	}
	
	public void setLearnMode(Boolean learnMode) {
		((RecordRecognitionSegmentatorOnline)getMultipleSegmentator()).setLearnMode(learnMode);
	}

}
