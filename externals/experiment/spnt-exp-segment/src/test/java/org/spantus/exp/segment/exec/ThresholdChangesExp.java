package org.spantus.exp.segment.exec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spantus.core.beans.SampleInfo;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
import org.spantus.exp.segment.draw.AbstractGraphGenerator;

public class ThresholdChangesExp extends AbstractGraphGenerator {

	protected String getGeneratePath() {
		return super.getGeneratePath() + "thrshld/";
	}

	@Override
	public List<ComparisionResult> compare() {
		List<ComparisionResult> results = new ArrayList<ComparisionResult>();
		MarkerSetHolder expert = getExpertMarkerSet();
		MarkerSet experMS = getWordMarkerSet(expert);
		IExtractorInputReader reader = getTestReader();

		ProcessReaderInfo processReaderInfo = new ProcessReaderInfo();

		Double[] threasholdArr = new Double[]{0.5, .7, 0.9, 1.0,1.1 ,1.2,1.3,1.4,1.5,1.6,1.7, 2.0, 3.0 }; 
		List<Double> threasholdList = Arrays.asList(threasholdArr);
		for (Double threashold : threasholdList) {
			String name="_threshold_" + threashold;
			
			processReaderInfo.setThresholdCoef(threashold);
			SampleInfo info = getProcessReader().processReader(reader,
					processReaderInfo);
			
			MarkerSet testMS = getSegmentator().extractSegments(info.getThresholds());
			
			ComparisionResult result = getMakerComparison().compare(experMS,
					testMS);
//			result.setTest(test)
			result.setName(name);
			results.add(result);
		}
		return results;
	}


	public static void main(final String[] args) {
		String expertMarksPath = DEFAULT_EXPERT_MARKS_PATH, testPath = DEFAULT_TEST_DATA_PATH;
		if(args.length  > 0){
			expertMarksPath = args[0];
		}
		if(args.length  > 1){
			testPath = args[1];
		}
		new ThresholdChangesExp().process(expertMarksPath, testPath);
	}


}
