package org.spantus.exp.segment.test;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.spantus.core.extractor.ExtractorOutputHolder;
import org.spantus.core.threshold.IThreshold;
import org.spantus.core.threshold.StaticThreshold;
import org.spantus.exp.segment.services.ExpServiceFactory;
import org.spantus.exp.segment.services.ProcessReader;

public class ProcessReaderTest extends TestCase {

	ProcessReader processReader;

	static int[] DISTRIBUTION_6 = { 6, 15, 20, 15, 6, 1 };
	static int element_size = 6;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		processReader = ExpServiceFactory.createProcessReader();
	}

	public void testGenerateSet() {

		Set<IThreshold> thresholds = createThresholdSet(element_size);

		for (int size = 1; size < element_size; size++) {
			Iterable<Set<String>> allCompbinations = processReader
					.generateAllCompbinations(thresholds, size);
			
			int[] curentDistribution = new int[size];
			for (Set<String> set : allCompbinations) {
				assertTrue("List too big: " + (set.size() - 1), size > set
						.size() - 1);
				curentDistribution[set.size() - 1]++;
			}
			int j = 0;
			for (int dist : curentDistribution) {
				assertEquals("Not the same for " + j, DISTRIBUTION_6[j++], dist);
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

	protected Set<IThreshold> createThresholdSet(int size) {
		Set<IThreshold> thresholds = new LinkedHashSet<IThreshold>();
		for (int j = 0; j < size; j++) {
			thresholds.add(createThreshold("threshold" + j));
		}
		return thresholds;
	}

	protected IThreshold createThreshold(String name) {
		ExtractorOutputHolder extractor = new ExtractorOutputHolder();
		extractor.setName(name);
		StaticThreshold threshold = new StaticThreshold();
		threshold.setExtractor(extractor);
		return threshold;

	}
}
