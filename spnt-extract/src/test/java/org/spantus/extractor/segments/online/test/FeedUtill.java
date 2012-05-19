package org.spantus.extractor.segments.online.test;

import java.util.Arrays;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.IClassificationListener;
import org.spantus.extractor.segments.online.ExtremeOnlineRuleClassifier;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.logger.Logger;

public final class FeedUtill {

	private static final Logger LOG = Logger.getLogger(FeedUtill.class);

	private FeedUtill() {
		// Intentionally
	}


	public static ExtremeOnlineRuleClassifier feedData(Double[] data,
			ClassifierRuleBaseService ruleBase) {
		logData(data);
		ExtremeOnlineRuleClassifier classifier = newExtremeOnlineRuleClassifier(ruleBase);
		feedData(data, classifier);

		return classifier;
	}
	
	public static ExtremeOnlineRuleClassifier feedData(Double[] data,
			ClassifierRuleBaseService ruleBase, IClassificationListener... classificationListeners) {
		logData(data);
		ExtremeOnlineRuleClassifier classifier = newExtremeOnlineRuleClassifier(ruleBase);
		for (IClassificationListener iClassificationListener : classificationListeners) {
			classifier.addClassificationListener(iClassificationListener);
		}
		
		feedData(data, classifier);

		return classifier;
	}
	

	public static void feedData(Double[] data,
			ExtremeOnlineRuleClassifier classifier) {
		long i = 0;
		for (Double aValue : data) {
			FrameValues windowValue = new FrameValues(100D);
			windowValue.add(aValue);
			windowValue.add(aValue + 1);
			windowValue.add(aValue + 3);
			windowValue.setFrameIndex(i);
			FrameValues values = new FrameValues();
			values.add(aValue);
			values.setFrameIndex(i++);
			classifier.afterCalculated(0L, windowValue, values);
		}

		classifier.flush();
	}

	public static ExtremeOnlineRuleClassifier newExtremeOnlineRuleClassifier(
			ClassifierRuleBaseService ruleBase) {
		ExtremeOnlineRuleClassifier classifier = new ExtremeOnlineRuleClassifier();
		classifier.setExtractor(new MockOnlineExtractor());
		classifier.getExtractor().getOutputValues().setSampleRate(100D);// 10ms
		classifier.setRuleBaseService(ruleBase);
		// classifier.setClusterService(clusterService);
		return classifier;
	}

	public static void logData(Double[] data) {
		LOG.debug("[logData] arr: \n" + Arrays.asList(data));
	}
}
