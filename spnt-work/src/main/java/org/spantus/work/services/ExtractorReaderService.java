/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.work.services;

import java.util.Map;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;


/**
 *
 * @author mondhs
 */
public interface ExtractorReaderService {
    public FrameVectorValues findFeatureVectorValuesForMarker(IExtractorInputReader reader, Marker marker, String featureName);
    public Map<String, FrameVectorValues> findAllVectorValuesForMarker(IExtractorInputReader reader, Marker marker);

}
