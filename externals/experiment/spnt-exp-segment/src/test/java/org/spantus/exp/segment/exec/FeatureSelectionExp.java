package org.spantus.exp.segment.exec;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.spantus.core.beans.SampleInfo;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.IThreshold;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
import org.spantus.exp.segment.draw.AbstractGraphGenerator;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;

public class FeatureSelectionExp extends AbstractGraphGenerator {

	protected String getGeneratePath() {
		return super.getGeneratePath() + "features/";
	}
	
	@Override
	public List<ComparisionResult> compare() {
		List<ComparisionResult> results = new ArrayList<ComparisionResult>();

		MarkerSet experMS = getWordMarkerSet(getExpertMarkerSet());
		Double thresholdCoef = 1.6;
		ProcessReaderInfo processReaderInfo = new ProcessReaderInfo();
		processReaderInfo.setThresholdCoef(thresholdCoef);
		SampleInfo info = getProcessReader().processReader(getTestReader(), processReaderInfo);

		Set<IThreshold> thresholds = new LinkedHashSet<IThreshold>();

		OnlineDecisionSegmentatorParam param = createDefaultOnlineParam();

		for (IThreshold threshold : info.getThresholds()) {
			thresholds.clear();
			thresholds.add(threshold);
			log.debug("start processing:" + thresholds);
			MarkerSet testMS = getSegmentator().extractSegments(thresholds, param);
			ComparisionResult result = getMakerComparison().compare(
					experMS, testMS);
			result.setThreshold(threshold);
			result.setName(getProcessReader().getName(threshold));
			results.add(result);
			log.debug("Result:" + result.getName()  + result.getParams());
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
		new FeatureSelectionExp().process(expertMarksPath, testPath);
	}
}
