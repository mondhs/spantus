package org.spantus.exp.segment.exec;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.spantus.core.beans.SampleInfo;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IClassifier;
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

		MarkerSetHolder experMSH = getExpertMarkerSet();
		Double thresholdCoef = 1.6;
		ProcessReaderInfo processReaderInfo = new ProcessReaderInfo();
		processReaderInfo.setThresholdCoef(thresholdCoef);
		SampleInfo info = getProcessReader().processReader(getTestReader(), processReaderInfo);

		Set<IClassifier> thresholds = new LinkedHashSet<IClassifier>();

		OnlineDecisionSegmentatorParam param =
//			new OnlineDecisionSegmentatorParam();
			createDefaultOnlineParam();

		for (IClassifier threshold : info.getThresholds()) {
			thresholds.clear();
			thresholds.add(threshold);
			log.debug("start processing:" + thresholds);
			MarkerSetHolder testMS = getSegmentator().extractSegments(thresholds, param);
			ComparisionResult result = getMakerComparison().compare(
					experMSH, testMS);
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
