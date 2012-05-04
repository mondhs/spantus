package org.spantus.core.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.marker.Marker;
import org.spantus.core.service.ExtractorInputReaderService;
import org.spantus.core.threshold.IClassifier;

public class ExtractorInputReaderServiceImpl implements
		ExtractorInputReaderService {

	public FrameVectorValues findFeatureVectorValuesForMarker(
			IExtractorInputReader reader, Marker marker, String featureName) {
		for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
			// extractors can have prefixes, just check if ends with
			if (!extractor.getName().endsWith(featureName)) {
				continue;
			}
			FrameVectorValues values = extractor.getOutputValues();
			Double fromIndex = (marker.getStart().doubleValue() * values
					.getSampleRate()) / 1000;
			Double toIndex = fromIndex
					+ (marker.getLength().doubleValue() * values
							.getSampleRate()) / 1000;
			FrameVectorValues fvv = values.subList(fromIndex.intValue(),
					toIndex.intValue());
			return fvv;
		}
		return null;
	}
	/**
	 * 
	 * @param reader
	 * @return
	 */
	public Set<IClassifier> extractClassifiers(IExtractorInputReader reader) {
		Set<IClassifier> classifiers = new HashSet<IClassifier>();
		for (IExtractor extractor : reader.getExtractorRegister()) {
			if (extractor instanceof IClassifier) {
				classifiers.add((IClassifier) extractor);
			}
		}
		return classifiers;
	}

	/**
	 * 
	 */
	public Map<String, IValues> findAllVectorValuesForMarker(
			IExtractorInputReader reader, Marker marker) {
		Map<String, IValues> result = new HashMap<String, IValues>();
		
		for (IGeneralExtractor<?> extractor : reader.getGeneralExtractor()){
			if (extractor.getName().endsWith("SIGNAL_EXTRACTOR")) {
				continue;
			}
			IValues values = extractor.getOutputValues();
			String key = preprocess(extractor.getName());
			IValues subList = subList(values, marker, extractor.getOffset());
			result.put(key, subList);
		}

		return result;
	}

	
	protected <T extends IValues> T subList(T values, Marker marker, long offset){
		int endIndex = values.size() - 1;
		// if(values.get(0).size()<=2){
		// continue;
		// }
		int fromIndex = values.toIndex(marker.getStart());
		fromIndex -= offset;
		int toIndex = values.toIndex(marker.getEnd());
		
		fromIndex = fromIndex < 0 ? 0 : fromIndex;
		toIndex -= offset;

		toIndex = endIndex < toIndex ? endIndex : toIndex;
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException(
					 " fromIndex(" + fromIndex + ") > toIndex(" + toIndex
					+ "); offset: " + offset);
		}

		T fvv = values.subList(fromIndex,toIndex);
		return fvv;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private String preprocess(String name) {
		return name.replace("BUFFERED_", "");
	}

	/**
     * 
     */
	public Map<String, IValues> findAllVectorValuesForMarker(
			IExtractorInputReader reader) {
		Map<String, IValues> result = new HashMap<String, IValues>();
		for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
			String key = preprocess(extractor.getName());
			result.put(key, extractor.getOutputValues());
		}
		for (IExtractor extractor : reader.getExtractorRegister()) {
			if (extractor.getName().endsWith("SIGNAL_EXTRACTOR")) {
				continue;
			}
			String key = preprocess(extractor.getName());
			result.put(key, extractor.getOutputValues());
		}
		return result;
	}

}
