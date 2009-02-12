package org.spantus.exp.segment.exec;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.spantus.core.beans.SampleInfo;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IThreshold;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
import org.spantus.exp.segment.draw.AbstractGraphGenerator;
import org.spantus.exp.segment.services.ProcessReader;
import org.spantus.exp.segment.services.impl.ProcessReaderImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.segment.offline.SimpleDecisionSegmentatorParam;

public class SegmentatorExp extends AbstractGraphGenerator {

	protected String getGeneratePath() {
		return super.getGeneratePath() + "sgmnts/";
	}

	@Override
	public List<ComparisionResult> compare() {
		List<ComparisionResult> results = new ArrayList<ComparisionResult>();
		MarkerSetHolder expert = getExpertMarkerSet();
		MarkerSet experMS = getWordMarkerSet(expert);

		ProcessReaderInfo pri = new ProcessReaderInfo();
		pri.setThresholdCoef(1.2 );
		SampleInfo info = getProcessReader().processReader(getTestReader(), pri);
		Set<IThreshold> set = info.getThresholds();
		info.getThresholds().retainAll(getProcessReader().getFilterThresholdByName(
				set, 
				ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR.name())
				);

		SimpleDecisionSegmentatorParam param = new SimpleDecisionSegmentatorParam();
		
		for (int i = 7; i < 17; i++) {
			param.setSegmentLengthThreshold(BigDecimal.valueOf(i*10).setScale(0));
			for (int j = 7; j < 17; j++) {
				String name = "_sgmnts_"
					+ param.getSegmentLengthThreshold() + "_" 
					+ param.getSegmentsSpaceThreshold();
				param.setSegmentsSpaceThreshold(BigDecimal.valueOf(j*10).setScale(0));
				MarkerSet testMS = getSegmentator().extractSegments(info.getThresholds(), param);
				log.debug("Will be processed: " + name);
				ComparisionResult result = getMakerComparison().compare(experMS,
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
