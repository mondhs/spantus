package org.spantus.extr.wordspot.domain;

import java.util.HashMap;
import java.util.Map;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.preemphasis.Preemphasis.PreemphasisEnum;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;

public class SegmentExtractorServiceConfig {

	private Map<String, ExtractorParam> extractorParams = new HashMap<String, ExtractorParam>();

	private PreemphasisEnum preephasis = PreemphasisEnum.full;

	private ClassifierEnum classifier = ClassifierEnum.online;

	private Float thresholdCoef = 3F;

	private SegmentatorServiceEnum segmentation = SegmentatorServiceEnum.basic;

	private OnlineDecisionSegmentatorParam segmentationParam;



	private ExtractorEnum[] extractors = new ExtractorEnum[] {
			ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR,
			ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
			ExtractorEnum.ENERGY_EXTRACTOR
	};

	private String repositoryPath;

	private int windowLength = 10;

	private int overlapInPerc = 33;

	public SegmentExtractorServiceConfig() {
		segmentationParam = new OnlineDecisionSegmentatorParam();
		segmentationParam.setMinSpace(0L);
		segmentationParam.setMinLength(0L);
		segmentationParam.setExpandStart(0L);
		segmentationParam.setExpandEnd(0L);
	}
	
	public Map<String, ExtractorParam> getExtractorParams() {
		return extractorParams;
	}

	public void setExtractorParams(Map<String, ExtractorParam> extractorParams) {
		this.extractorParams = extractorParams;
	}

	public ClassifierEnum getClassifier() {
		return classifier;
	}

	public void setClassifier(ClassifierEnum classifier) {
		this.classifier = classifier;
	}

	public ExtractorEnum[] getExtractors() {
		return extractors;
	}

	public void setExtractors(ExtractorEnum[] extractors) {
		this.extractors = extractors;
	}

	public OnlineDecisionSegmentatorParam getSegmentationParam() {
		return segmentationParam;
	}

	public void setSegmentationParam(
			OnlineDecisionSegmentatorParam segmentationParam) {
		this.segmentationParam = segmentationParam;
	}

	public SegmentatorServiceEnum getSegmentation() {
		return segmentation;
	}

	public void setSegmentation(SegmentatorServiceEnum segmentation) {
		this.segmentation = segmentation;
	}

	public PreemphasisEnum getPreephasis() {
		return preephasis;
	}

	public void setPreephasis(PreemphasisEnum preephasis) {
		this.preephasis = preephasis;
	}

	public Float getThresholdCoef() {
		return thresholdCoef;
	}

	public void setThresholdCoef(Float thresholdCoef) {
		this.thresholdCoef = thresholdCoef;
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public int getWindowLength() {
		return windowLength;
	}

	public void setWindowLength(int windowLength) {
		this.windowLength = windowLength;
	}

	public int getOverlapInPerc() {
		return overlapInPerc;
	}

	public void setOverlapInPerc(int overlapInPerc) {
		this.overlapInPerc = overlapInPerc;
	}
}
