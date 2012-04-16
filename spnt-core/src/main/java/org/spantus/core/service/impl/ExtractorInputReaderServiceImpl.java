package org.spantus.core.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
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

	public Map<String, IValues> findAllVectorValuesForMarker(
			IExtractorInputReader reader, Marker marker) {
		Map<String, IValues> result = new HashMap<String, IValues>();

		for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
			// extractors can have prefixes, jus check if ends with
			FrameVectorValues values = extractor.getOutputValues();
			int endIndex = values.size() - 1;
			// if(values.get(0).size()<=2){
			// continue;
			// }
			Double fromIndex = (marker.getStart().doubleValue() * values
					.getSampleRate()) / 1000;
			fromIndex -= extractor.getOffset();
			fromIndex = fromIndex < 0 ? 0 : fromIndex;

			Double toIndex = fromIndex
					+ (marker.getLength().doubleValue() * values
							.getSampleRate()) / 1000;
			toIndex -= extractor.getOffset();

			toIndex = endIndex < toIndex ? endIndex : toIndex;
			if (fromIndex > toIndex) {
				throw new IllegalArgumentException(extractor.getName()
						+ " fromIndex(" + fromIndex + ") > toIndex(" + toIndex
						+ "); offset: " + extractor.getOffset());
			}

			FrameVectorValues fvv = values.subList(fromIndex.intValue(),
					toIndex.intValue());
			String key = preprocess(extractor.getName());
			result.put(key, fvv);
		}
		for (IExtractor extractor : reader.getExtractorRegister()) {
			if (extractor.getName().endsWith("SIGNAL_EXTRACTOR")) {
				continue;
			}
			// extractors can have prefixes, just check if ends with
			FrameValues values = extractor.getOutputValues();
			int endIndex = values.size() - 1;
			Double fromIndex = (marker.getStart().doubleValue() * values
					.getSampleRate()) / 1000;
			fromIndex = fromIndex < 0 ? 0 : fromIndex;
			Double toIndex = fromIndex
					+ (marker.getLength().doubleValue() * values
							.getSampleRate()) / 1000;
			toIndex = endIndex < toIndex ? endIndex : toIndex;
			if (fromIndex > toIndex) {
				throw new IllegalArgumentException("Nonsence");
			}
			FrameValues fv = values.subList(fromIndex.intValue(),
					toIndex.intValue());
			String key = preprocess(extractor.getName());
			result.put(key, fv);
		}

		return result;
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
