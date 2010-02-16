package org.spantus.exp.segment.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.spantus.core.beans.SampleInfo;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IClassifier;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
import org.spantus.exp.segment.draw.AbstractGraphGenerator;
import org.spantus.exp.segment.services.ProcessReader;
import org.spantus.exp.segment.services.impl.ProcessReaderImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.segment.offline.BaseDecisionSegmentatorParam;

public class SegmentatorExp extends AbstractGraphGenerator {

	protected String getGeneratePath() {
		return super.getGeneratePath() + "sgmnts/";
	}

	@Override
	public List<ComparisionResult> compare() {
		List<ComparisionResult> results = new ArrayList<ComparisionResult>();
		MarkerSetHolder expert = getExpertMarkerSet();
		MarkerSetHolder experMSH = expert;

		ProcessReaderInfo pri = new ProcessReaderInfo();
		pri.setThresholdCoef(1.2 );
		SampleInfo info = getProcessReader().processReader(getTestReader(), pri);
		Set<IClassifier> set = info.getThresholds();
		info.getThresholds().retainAll(getProcessReader().getFilterThresholdByName(
				set, 
				ExtractorEnum.ENERGY_EXTRACTOR.name())
				);

		BaseDecisionSegmentatorParam param = new BaseDecisionSegmentatorParam();
		
		for (int i = 7; i < 17; i++) {
			param.setMinLength(i*10L);
			for (int j = 7; j < 17; j++) {
				String name = "_sgmnts_"
					+ param.getMinLength() + "_" 
					+ param.getMinSpace();
				param.setMinSpace(j*10L);
				MarkerSetHolder testMS = getSegmentator().extractSegments(info.getThresholds(), param);
				log.debug("Will be processed: " + name);
				ComparisionResult result = getMakerComparison().compare(experMSH,
						testMS);
				result.setName(name);
				results.add(result);
				
			}
		}
		return results;
	}

	public ProcessReader getProcessReader(ProcessReaderInfo processReaderInfo) {
		ProcessReaderImpl impl = new ProcessReaderImpl();
		return impl;
	}

	public static void main(final String[] args) {
		String expertMarksPath = DEFAULT_EXPERT_MARKS_PATH, testPath = DEFAULT_TEST_DATA_PATH;
		if(args.length  > 0){
			expertMarksPath = args[0];
		}
		if(args.length  > 1){
			testPath = args[1];
		}
		new SegmentatorExp().process(expertMarksPath, testPath);
	}


}
