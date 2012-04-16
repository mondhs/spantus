package org.spantus.core.service;

import java.util.Map;
import java.util.Set;

import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.IClassifier;

/**
 * 
 * @author mgreibus
 * @since 0.3
 * 
 * 
 */
public interface ExtractorInputReaderService {
	public IValues findFeatureVectorValuesForMarker(
			IExtractorInputReader reader, Marker marker, String featureName);

	public Map<String, IValues> findAllVectorValuesForMarker(
			IExtractorInputReader reader, Marker marker);

	public Map<String, IValues> findAllVectorValuesForMarker(
			IExtractorInputReader reader);
	
	public Set<IClassifier> extractClassifiers(IExtractorInputReader reader);

}
