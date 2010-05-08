package org.spantus.exp.segment.services.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.core.io.SignalReader;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.StringUtils;
import org.spantus.work.io.WorkAudioFactory;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.2
 * Created May 4, 2010
 *
 */
public class ComarisionFacadeImpl implements ComarisionFacade {
	
	private Map<String, ExtractorParam> extractorParams;
	private ClassifierEnum classifier = ClassifierEnum.rules;
	private SegmentatorServiceEnum segmentation = SegmentatorServiceEnum.online;
	
	/* (non-Javadoc)
	 * @see org.spantus.exp.segment.services.impl.ComarisionFacade#readSignal(java.util.List, org.spantus.extractor.impl.ExtractorEnum[])
	 */
	public IExtractorInputReader readSignal(List<String> wavName, ExtractorEnum[] extractors) {
		try {
			List<URL> urlFiles = new ArrayList<URL>();
			for (String name : wavName) {
				if (StringUtils.hasText(name)) {
					File wavFile = new File(name);
					urlFiles.add(wavFile.toURI().toURL());
				}
			}
			SignalReader reader = WorkAudioFactory.createAudioReader(urlFiles
					.get(0));
			SignalFormat format = reader.getFormat(urlFiles.get(0));
			IExtractorConfig config = ExtractorConfigUtil.defaultConfig(format
					.getSampleRate(), 30, 66);
			IExtractorInputReader bufferedReader = ExtractorsFactory
					.createReader(config);

			ExtractorUtils
					.registerThreshold(
							bufferedReader,
							extractors,
							getExtractorParams(), getClassifier());
			reader.readSignal(urlFiles, bufferedReader);
			return bufferedReader;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}
	/* (non-Javadoc)
	 * @see org.spantus.exp.segment.services.impl.ComarisionFacade#calculateMarkers(java.util.List, org.spantus.extractor.impl.ExtractorEnum[], org.spantus.segment.online.OnlineDecisionSegmentatorParam)
	 */
	public MarkerSetHolder calculateMarkers(List<String> wavName, ExtractorEnum[] extractors, OnlineDecisionSegmentatorParam param) {
		IExtractorInputReader reader = readSignal(wavName, extractors);
		MarkerSetHolder testMarkerSet = extractSegments(
				ExtractorUtils.filterOutClassifers(reader), param);
		return testMarkerSet;
	}
	/**
	 * extractuion with params
	 * 
	 * @param classifiers
	 * @return
	 */
	protected MarkerSetHolder extractSegments(Set<IClassifier> classifiers, OnlineDecisionSegmentatorParam param) {
		MarkerSetHolder testMarkerSet = null;
		ISegmentatorService online = (ISegmentatorService) SegmentFactory
				.createSegmentator(getSegmentation().name());
		testMarkerSet = online.extractSegments(classifiers, param);
		return testMarkerSet;
	}
	
	/////////////// Getters and setters
	
	public Map<String, ExtractorParam> getExtractorParams() {
		return extractorParams;
	}
	public void setExtractorParams(Map<String, ExtractorParam> params) {
		this.extractorParams = params;
	}
	public ClassifierEnum getClassifier() {
		return classifier;
	}
	public void setClassifier(ClassifierEnum classifier) {
		this.classifier = classifier;
	}
	public SegmentatorServiceEnum getSegmentation() {
		return segmentation;
	}
	public void setSegmentation(SegmentatorServiceEnum segmentation) {
		this.segmentation = segmentation;
	}
}
