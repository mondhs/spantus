package org.spantus.extractor.segments.likelihood;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.logger.Logger;
import org.spantus.math.LPCResult;
import org.spantus.math.MatrixUtils;
import org.spantus.math.services.LPCService;
import org.spantus.math.services.MathServicesFactory;
import org.spantus.math.windowing.HammingWindowing;
@Ignore
public class PartialLikelihoodFunctionCalculationTest {

	// private LPCExtractor extractor;
	private LPCService lpcService;
	private PartialLikelihoodFunctionCalculation calculation;

	public static final int ORDER = 12;
	public static final int NUMBER_OF_SELECTED_FRAMES = 10;

	private Logger log = Logger
			.getLogger(PartialLikelihoodFunctionCalculationTest.class);
	private FrameValues window;
	private FrameVectorValues feature;
	private LPCResult lpc;

	public PartialLikelihoodFunctionCalculationTest() {
		// extractor = new LPCExtractor();
		lpcService = MathServicesFactory.createLPCService();
		calculation = new PartialLikelihoodFunctionCalculation();
	}

	@Before
	public void onsetup() {

		window = new FrameValues();
		HammingWindowing windowing = new HammingWindowing();
		int windowSize = 21;
		for (int i = 1; i < windowSize; i++) {
			Double value = Math.cos(i * .31 * Math.PI / 2);
			value *= windowing.calculate(windowSize, i);
			window.add(value);
		}

		feature = new FrameVectorValues();
		lpc = getLpcService().calculateLPC(window, ORDER);
		feature.add(lpc.getResult());

	}

	/**
	 * 
	 */
	@Test
	public void testPartialLikelihoodFunctionCalculation() {
		// given
		FrameValues samples = new FrameValues();
		FrameVectorValues featureValues = new FrameVectorValues();
		int numberOfPhonemes = NUMBER_OF_SELECTED_FRAMES;

		for (int i = 0; i < NUMBER_OF_SELECTED_FRAMES; i++) {
			samples.addAll(window);
			featureValues.addAll(feature);
		}

		// when
		List<LinkedList<Double>> partialLikelihoodFunctions = calculation
				.processPartialLikelihoodFunctionCalculation(samples,
						featureValues, ORDER, NUMBER_OF_SELECTED_FRAMES,
						lpc.getReflection());

		@SuppressWarnings("unused")
		List<LinkedList<Double>> bellmanFunctions = calculation
				.processBellmanFunctionCalculation(samples, ORDER,
						numberOfPhonemes, partialLikelihoodFunctions);

		// then
		Assert.assertEquals(NUMBER_OF_SELECTED_FRAMES - 1,
				partialLikelihoodFunctions.size());
		Assert.assertEquals(samples.size(), partialLikelihoodFunctions.get(0)
				.size());

		
		StringBuilder sb = MatrixUtils.toStringTranform(partialLikelihoodFunctions);
		log.debug("partialLikelihoodFunctions: \n {0}", sb);
		// for (Float float1 : samples) {
		// sb.append(float1).append("\n");
		// }
	
		
	}

	public LPCService getLpcService() {
		return lpcService;
	}

}
