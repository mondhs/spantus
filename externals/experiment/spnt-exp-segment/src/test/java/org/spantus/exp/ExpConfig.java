package org.spantus.exp;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.ExtractorParamUtils;

public class ExpConfig {

	public static final int WINDOW_OVERLAP = 33;
	public static final int WINDOW_LENGTH = 10;

	
	public final static String EXP_ROOT_PATH= "/home/as/tmp/garsyno.modelis";
	public final static String EXP_TRAIN_DIR= "TRAIN";
	public final static String EXP_CORPUS_DIR= "CORPUS";
	private static final String EXP_TEST_DIR = "TEST";
	
	public final static String DIR_LEARN_WAV =
	// "/mnt/audio/VDU_ISO4"
	// "/home/mgreibus/src/garsynai/garsynai/noizeus_exp"
//	"/home/mgreibus/src/garsynai/VDU/MG/"
	// "/home/mondhs/src/garsynai/skaiciai/learn"
//			"/home/as/src/garsynai/VDU/MG/"
			""
	;
	public final static String DIR_LEARN_OUT =
			""
//			"/home/mgreibus/tmp/garsyno.modelis/TRAIN";
	// "/home/mgreibus/src/garsynai/garsynai/noizeus_exp/OUTPUT"
	// "/home/mgreibus/src/garsynai/VDU/MG/OUTPUT"
	// "./target/learn-corpus/"
	;
	public final static ExtractorEnum[] EXP_EXTRACTORS = new ExtractorEnum[]{
            ExtractorEnum.MFCC_EXTRACTOR,
            ExtractorEnum.PLP_EXTRACTOR,
            ExtractorEnum.LPC_EXTRACTOR,
//            ExtractorEnum.FFT_EXTRACTOR,
            ExtractorEnum.LOUDNESS_EXTRACTOR,
            ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
            ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR};

	public final static String RULES_PATH = 
//			"/home/as/src/spantus-svn/trunk/spnt-work-ui/src/main/resources/" +
			"/ClassifierRuleBase.csv";
	
	private int windowLength;
	private int windowOverlap;
	private String rulePath;
	private String dirLearn;
	private String rootPath;
	private String trainDir;
	private String corpusDir;
	private String audioFilePrefix;
	private String markerFilePrefix;
	private ExtractorEnum[] extractors;
	private OnlineDecisionSegmentatorParam segmentationParam;
	private Map<String, ExtractorParam> extractorPramMap;
	private String testDir;
	private String segmentatorServiceType;
	private ClassifierEnum classifier;
	private double threasholdCoef = 5.0;
	private ExtractorModifiersEnum modifier = ExtractorModifiersEnum.mean;
	
	public static ExpConfig createConfig() {
		ExpConfig config = new ExpConfig();
		config.setSegmentatorServiceType(SegmentatorServiceEnum.basic.name());
		config.setWindowLength(WINDOW_LENGTH);
		config.setWindowOverlap(WINDOW_OVERLAP);
		config.setRulePath(RULES_PATH);
		config.setDirLearn(DIR_LEARN_OUT);
		config.setRootPath(EXP_ROOT_PATH);
		config.setTrainDir(EXP_TRAIN_DIR);
		config.setCorpusDir(EXP_CORPUS_DIR);
		config.setTestDir(EXP_TEST_DIR);
		config.setExtractors(EXP_EXTRACTORS);
		config.setClassifier(ClassifierEnum.online);
		config.setAudioFilePrefix(".wav");
		config.setMarkerFilePrefix(".mspnt.xml");
		OnlineDecisionSegmentatorParam segmentationParam = new OnlineDecisionSegmentatorParam();
		segmentationParam.setMinLength(91L);
		segmentationParam.setMinSpace(261L);
		segmentationParam.setExpandStart(260L);
		segmentationParam.setExpandEnd(360L);
        config.setSegmentationParam(segmentationParam);
        config.setExtractorPramMap( new HashMap<String, ExtractorParam>());

        config = updateCoefAndModifier(config, config.getThreasholdCoef(), config.getModifier());
		
		
		return config;
	}
	/**
	 * 
	 * @param Double
	 * @return
	 */
	private static ExpConfig updateCoefAndModifier(ExpConfig config,  Double aCoef, ExtractorModifiersEnum  aModifer) {
		config.threasholdCoef = aCoef;
		config.modifier = aModifer;
		
		ExtractorParam theParam = new ExtractorParam();
//		ExtractorParam thresholdCoefParam = new ExtractorParam();
		ExtractorParamUtils.setValue(theParam, aModifer.name(), Boolean.TRUE);
		ExtractorParamUtils.setValue(theParam, ExtractorParamUtils.commonParam.threasholdCoef.name(), aCoef);

		ExtractorEnum[] enumInuse = new ExtractorEnum[]{ExtractorEnum.LOUDNESS_EXTRACTOR,ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR };
		for (ExtractorEnum extractorEnum : enumInuse) {
			config.getExtractorPramMap().put(extractorEnum.name(), theParam);
		}
		return config;
	}

	private void setExtractors(ExtractorEnum[] extractors) {
		this.extractors = extractors;
	}

	public void setWindowLength(int windowLength) {
		this.windowLength = windowLength;
	}

	public int getWindowLength() {
		return windowLength;
	}

	public void setWindowOverlap(int windowOverlap) {
		this.windowOverlap = windowOverlap;
	}

	public int getWindowOverlap() {
		return windowOverlap;
	}

	public String getRulePath() {
		return rulePath;
	}

	public void setRulePath(String rulePath) {
		this.rulePath = rulePath;
	}

	public String getDirLearn() {
		return dirLearn;
	}
	public File getDirLearnAsFile() {
		return new File(dirLearn);
	}

	public void setDirLearn(String dirLearn) {
		this.dirLearn = dirLearn;
	}

	public File getMarkerDir() {
		return null;
	}

	public String getWavDir() {
		return null;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	
	public File getTrainDirAsFile() {
		return new File(getRootPath(), getTrainDir());
	}

	public String getTrainDir() {
		return trainDir;
	}

	public void setTrainDir(String trainDir) {
		this.trainDir = trainDir;
	}

	public File getCorpusDirAsFile() {
		return new File(getRootPath(), getCorpusDir());
	}
	
	public String getCorpusDir() {
		return corpusDir;
	}

	public void setCorpusDir(String corpusDir) {
		this.corpusDir = corpusDir;
	}

	public ExtractorEnum[] getExtractors() {
		return extractors;
	}


	public OnlineDecisionSegmentatorParam getSegmentationParam() {
		return segmentationParam;
	}

	public void setSegmentationParam(
			OnlineDecisionSegmentatorParam segmentationParam) {
		this.segmentationParam = segmentationParam;
	}

	public String getTestDir() {
		return testDir;
	}
	public File getTestDirAsFile() {
		return new File(getRootPath(),getTestDir());
	}

	
	public Map<String, ExtractorParam> getExtractorPramMap() {
		return extractorPramMap;
	}

	public void setExtractorPramMap(Map<String, ExtractorParam> extractorPramMap) {
		this.extractorPramMap = extractorPramMap;
	}

	public void setTestDir(String testDir) {
		this.testDir = testDir;
	}

	public String getSegmentatorServiceType() {
		return segmentatorServiceType;
	}

	public void setSegmentatorServiceType(String segmentatorServiceType) {
		this.segmentatorServiceType = segmentatorServiceType;
	}

	public ClassifierEnum getClassifier() {
		return classifier;
	}

	public void setClassifier(ClassifierEnum classifier) {
		this.classifier = classifier;
	}

	public double getThreasholdCoef() {
		return threasholdCoef;
	}

	public void setThreasholdCoef(double threasholdCoef) {
		updateCoefAndModifier(this, threasholdCoef, getModifier());
		this.threasholdCoef = threasholdCoef;
	}
	public ExtractorModifiersEnum getModifier() {
		return modifier;
	}
	public void setModifier(ExtractorModifiersEnum modifier) {
		updateCoefAndModifier(this, getThreasholdCoef(), modifier);
		this.modifier = modifier;
	}
	public String getAudioFilePrefix() {
		return audioFilePrefix;
	}
	public void setAudioFilePrefix(String audioFilePrefix) {
		this.audioFilePrefix = audioFilePrefix;
	}
	public String getMarkerFilePrefix() {
		return markerFilePrefix;
	}
	public void setMarkerFilePrefix(String markerFilePrefix) {
		this.markerFilePrefix = markerFilePrefix;
	}
	@Override
	public String toString() {
		return "ExpConfig [windowLength=" + windowLength + ", windowOverlap="
				+ windowOverlap + ", modifier=" + modifier + ", extractors="
				+ Arrays.toString(extractors) + ", extractorPramMap="
				+ extractorPramMap + ", rootPath=" + rootPath + "]";
	}

}
