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

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.io.RecordWraperExtractorReader;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.externals.recognition.segment.RecordRecognitionSegmentatorOnline;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.segment.online.ISegmentatorListener;
import org.spantus.segment.online.MultipleSegmentatorListenerOnline;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.work.services.ConfigPropertiesDao;
import org.spantus.work.ui.AbstractSegmentPlot;
import org.spantus.work.ui.SegmentMonitorPlot;
import org.spnt.recognition.dtw.ui.WritableCorpusMatchListener;

public class RecognitionMonitorPlot extends SegmentMonitorPlot {
	
	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected MultipleSegmentatorListenerOnline createSegmentatorRecordable(){
		RecordRecognitionSegmentatorOnline multipleSegmentator = new RecordRecognitionSegmentatorOnline();
		multipleSegmentator.setCorpusMatchListener(new WritableCorpusMatchListener());
		multipleSegmentator.setParam(createParam());
		multipleSegmentator.setReader((RecordWraperExtractorReader)getWraperExtractorReader());
		return multipleSegmentator;
	}
	
	@Override
	public void registerExtractors(ExtractorParam param, ISegmentatorListener multipleSegmentator){
		IClassifier segmentator = null;

		ExtractorParam paramEnergy = new ExtractorParam();
		Boolean smooth = (Boolean) param.getProperties().get(ConfigPropertiesDao.key_segmentation_modifier_smooth);
		Boolean mean = (Boolean) param.getProperties().get(ConfigPropertiesDao.key_segmentation_modifier_mean);
		String classifierStr = (String) param.getProperties().get(ConfigPropertiesDao.key_segmentation_classifier);
		ClassifierEnum classifier = ClassifierEnum.valueOf(classifierStr);
		
		ExtractorParamUtils.setValue(paramEnergy, 
				ExtractorModifiersEnum.smooth.name(), Boolean.TRUE.equals(smooth));
		ExtractorParamUtils.setValue(paramEnergy, 
				ExtractorModifiersEnum.mean.name(), Boolean.TRUE.equals(mean));

		segmentator =ExtractorUtils.registerThreshold(getReader(), ExtractorEnum.ENERGY_EXTRACTOR, paramEnergy ,classifier); 
		segmentator.addClassificationListener(multipleSegmentator);
		segmentator  = ExtractorUtils.registerThreshold(getReader(), ExtractorEnum.MFCC_EXTRACTOR);

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
