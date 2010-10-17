/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.services;

import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.marker.Marker;

/**
 *
 * @author mondhs
 */
public class ExtractorReaderServiceImpl implements ExtractorReaderService {

    public FrameVectorValues findFeatureVectorValuesForMarker(IExtractorInputReader reader,
            Marker marker, String featureName) {
        for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
            //extractors can have prefixes, jus check if ends with
            if(! extractor.getName().endsWith(featureName)){
                continue;
            }
            FrameVectorValues values = extractor.getOutputValues();
            Float fromIndex = (marker.getStart().floatValue() * values.getSampleRate()) / 1000;
            Float toIndex = fromIndex + (marker.getLength().floatValue() * values.getSampleRate()) / 1000;
            FrameVectorValues fvv = values.subList(fromIndex.intValue(), toIndex.intValue());
            return fvv;
        }
        return null;
    }
}
