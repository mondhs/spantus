package org.spantus.exp.segment.test;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.spantus.core.extractor.ExtractorOutputHolder;
import org.spantus.core.threshold.IClassifier;
import org.spantus.core.threshold.StaticThreshold;
import org.spantus.exp.segment.services.ExpServiceFactory;
import org.spantus.exp.segment.services.ProcessReader;

public class ProcessReaderTest {

	ProcessReader processReader;

	static int[] DISTRIBUTION_6 = { 6, 15, 20, 15, 6, 1 };
	static int element_size = 6;

	@Before
	public void setUp() throws Exception {
		processReader = ExpServiceFactory.createProcessReader();
	}

	@Test @Ignore
	public void testGenerateSet() {

		Set<IClassifier> thresholds = createThresholdSet(element_size);

		for (int size = 1; size < element_size; size++) {
			Iterable<Set<String>> allCompbinations = processReader
					.generateAllCompbinations(thresholds, size);
			
			int[] curentDistribution = new int[size];
			for (Set<String> set : allCompbinations) {
				Assert.assertTrue("List too big: " + (set.size() - 1), size > set
						.size() - 1);
				curentDistribution[set.size() - 1]++;
			}
			int j = 0;
			for (int dist : curentDistribution) {
				Assert.assertEquals("Not the same for " + j, DISTRIBUTION_6[j++], dist);
			}
		}
	}
	
	protected int sum(int size) {
		int sum = 0;
		for (int i = 0; i < size; i++) {
			sum += DISTRIBUTION_6[i];
		}
		return sum;
	}

	protected Set<IClassifier> createThresholdSet(int size) {
		Set<IClassifier> thresholds = new LinkedHashSet<IClassifier>();
		for (int j = 0; j < size; j++) {
			thresholds.add(createThreshold("threshold" + j));
		}
		return thresholds;
	}

	protected IClassifier createThreshold(String name) {
		ExtractorOutputHolder extractor = new ExtractorOutputHolder();
		extractor.setName(name);
		StaticThreshold threshold = new StaticThreshold();
		threshold.setExtractor(extractor);
		return threshold;

	}
}
