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
package org.spantus.externals.recognition.ui;

import org.spantus.core.io.RecordWraperExtractorReader;
import org.spantus.externals.recognition.segment.RecordRecognitionSegmentatorOnline;
import org.spantus.segment.online.MultipleSegmentatorListenerOnline;
import org.spantus.work.ui.AbstractSegmentPlot;
import org.spantus.work.ui.SegmentMonitorPlot;

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
