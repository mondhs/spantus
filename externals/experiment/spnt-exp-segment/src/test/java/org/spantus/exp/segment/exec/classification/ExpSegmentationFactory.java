package org.spantus.exp.segment.exec.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.utils.CollectionUtils;
import org.spantus.utils.FileUtils;

public abstract class ExpSegmentationFactory {
	public static List<ComparisionResult> acceleromerData(String... nodes) {
		List<ComparisionResult> results = new ArrayList<ComparisionResult>();
		for (String node : nodes) {
			results.addAll(acceleromerData(node));
		}
		return results;
	}

	/**
	 * 
	 */
	public static List<ComparisionResult> acceleromerData(String node) {
		ExpSegmentation expSegmentation = new ExpSegmentation();
		expSegmentation.init();
		expSegmentation
				.setExtractors(new ExtractorEnum[] { ExtractorEnum.ENERGY_EXTRACTOR });
		String root = ExpSegmentationUtil.NOIZEUS_ROOT + "data/";
		String signalName = root + node + ".txt";
		String markerName = root + node + "_system.mspnt.xml";
		root += "noises/";
		String[] noisesArr = new String[] { null,
				root + "accelerometer.noises.txt",
				root + "accelerometer.noises.1-2.txt",
				root + "accelerometer.noises.2-0.txt",
				root + "accelerometer.noises.5-0.txt",
				root + "accelerometer.noises.10-0.txt" };
		List<String> noises = CollectionUtils.toList(noisesArr);
		ExpCriteria expCriteria = new ExpCriteria();
		expCriteria.setSignalNames(CollectionUtils.toList(signalName));
		expCriteria.setNoiseNames(noises);
		expCriteria.setMarkerName(markerName);

		// expSegmentation.getParam().setExpandEnd(30L);
		// expSegmentation.getParam().setExpandStart(30L);

		List<ComparisionResult> results = expSegmentation
				.multipleMixtureExperiment(expCriteria);
		return results;
	}
	
	
	/**
	 * 
	 * @param enum1
	 * @return
	 */
	public static ExpSegmentation createWavExpSegmentation(ExpSegmentation expSegmentation, ClassifierEnum enum1) {
		expSegmentation = createWavExpSegmentation(expSegmentation);
		expSegmentation.getComarisionFacade().setClassifier(enum1);
		return expSegmentation;
	}

	/**
	 * 
	 * @return
	 */
	public static ExpSegmentation createWavExpSegmentation(ExpSegmentation expSegmentation) {
		
		if(expSegmentation == null){
			expSegmentation = new ExpSegmentation();
		}
		
		expSegmentation.init();

		expSegmentation
				.setExtractors(new ExtractorEnum[] {
                    ExtractorEnum.LOUDNESS_EXTRACTOR,
                    ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
                    ExtractorEnum.LPC_RESIDUAL_EXTRACTOR,
                    ExtractorEnum.NOISE_LEVEL_EXTRACTOR,
                    ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR,
                    ExtractorEnum.ENERGY_EXTRACTOR,
                    ExtractorEnum.ENVELOPE_EXTRACTOR,
                });
		expSegmentation.getParam().setMinSpace(20L);
		expSegmentation.getParam().setMinLength(0L);
		expSegmentation.getParam().setExpandEnd(0L);
		expSegmentation.getParam().setExpandStart(0L);

		Map<String, ExtractorParam> extractorParams = new HashMap<String, ExtractorParam>();

		// ExtractorParam extractorParam =
		// createExtractorParam(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
		// ExtractorParamUtils.commonParam.threasholdCoef, 10F);
		// extractorParams = applyParams(extractorParams, extractorParam);

		// extractorParam =
		// createExtractorParam(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
		// ExtractorModifiersEnum.stdev, Boolean.TRUE);
		// extractorParams = applyParams(extractorParams, extractorParam);

		expSegmentation.getComarisionFacade().setExtractorParams(
				extractorParams);

		return expSegmentation;
	}

	public static List<ExpCriteria> createNoizeusExpCriterias(String... nodes) {
		List<ExpCriteria> results = new ArrayList<ExpCriteria>();
		for (String node : nodes) {
			results.add(createNoizeusExpCriteria(node));
		}
		return results;
	}

	/**
	 * 
	 */
	public static ExpCriteria createNoizeusExpCriteria(String node) {
//		ExpSegmentation expSegmentation = createWavExpSegmentation();

		String root = ExpSegmentationUtil.NOIZEUS_ROOT + node;
		String markerName = FileUtils.findFirstMatchFullPath(root, "mspnt.xml");
		List<String> signalList = FileUtils.findAllMatchFullPath(root, "wav");

		ExpCriteria expCriteria = new ExpCriteria();
		expCriteria.setSignalNames(signalList);
		expCriteria.setNoiseNames(null);
		expCriteria.setMarkerName(markerName);

//		List<ComparisionResult> results = expSegmentation
//		.multipleMixtureExperiment(expCriteria);

		return expCriteria;
	}
}
