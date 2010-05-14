package org.spantus.exp.segment.exec.classification;

import java.util.ArrayList;
import java.util.List;

import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.services.impl.ExperimentHsqlDao;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.utils.FileUtils;
import org.spantus.utils.StringUtils;


public class ExpDBSegmentation extends ExpSegmentation{
	
	ExperimentHsqlDao experimentDao = new ExperimentHsqlDao();
	public ExpDBSegmentation() {
		experimentDao.init();
	}
	
	@Override
	public void generateExpName(ComparisionResult result, List<String> signals){
		super.generateExpName(result, signals);
		result.setClassifier(getComarisionFacade().getClassifier().name());
		String experimentName = result.getName();
		StringBuilder features = new StringBuilder();
		String iseperator = "";
		for (ExtractorEnum enum1 : getExtractors()) {
			features.append(iseperator).append(enum1.getDisplayName());
			iseperator = "-";
		}
		
		for (String signal : signals) {
			if(StringUtils.hasText(signal)){
				String fileName = FileUtils.truncateDir(signal).replaceAll(".wav", "");
				if(!StringUtils.hasText(result.getNoiseLevel())){
					String[] parts = fileName.split("_");
					if(parts.length > 0){
						result.setName(parts[0]);
					}
					if(parts.length > 1){
						result.setNoiseType(parts[1]);
					}
					if(parts.length > 2){
						result.setNoiseLevel(parts[2]);
					}
					break;
				}
			}
		}

		
		experimentDao.save(result, features.toString(), null, result.getName());
	}
	/**
	 * 
	 * @return
	 */
	public static List<ComparisionResult> calcResultForAllNoizeus() {
		List<ComparisionResult> results = new ArrayList<ComparisionResult>();
		List<ExpCriteria> criterias = null;

		ClassifierEnum[] enums = new ClassifierEnum[] { ClassifierEnum.dynamic,
				ClassifierEnum.offline, ClassifierEnum.rules,
				ClassifierEnum.rulesOnline };
		String[] noizes = new String[]{
				ExpSegmentationUtil.NOIZEUS_01,
				ExpSegmentationUtil.NOIZEUS_02,
				ExpSegmentationUtil.NOIZEUS_04,
				ExpSegmentationUtil.NOIZEUS_07,
				ExpSegmentationUtil.NOIZEUS_10,
				ExpSegmentationUtil.NOIZEUS_21};
		
		for (ClassifierEnum classifierEnum : enums) {
			ExpSegmentation expSegmentation = ExpSegmentationFactory
					.createWavExpSegmentation(new ExpDBSegmentation(), classifierEnum);
			criterias = ExpSegmentationFactory.createNoizeusExpCriterias(noizes);
			results.addAll(expSegmentation
					.multipleMixtureExperiments(criterias));
		}
		return results;
	}
	
	
//////////////////////////////////////////MAIN
	public static void main(String[] args) {
		List<ComparisionResult> results = calcResultForAllNoizeus();
		System.err.print("DUN");
	}
}
