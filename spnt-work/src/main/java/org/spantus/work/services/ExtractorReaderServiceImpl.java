/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.work.services;

import java.util.HashMap;
import java.util.Map;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.marker.Marker;
import org.spantus.logger.Logger;

/**
 *
 * @author mondhs
 */
public class ExtractorReaderServiceImpl implements ExtractorReaderService {

    private static Logger log = Logger.getLogger(ExtractorReaderServiceImpl.class);

    public FrameVectorValues findFeatureVectorValuesForMarker(IExtractorInputReader reader,
            Marker marker, String featureName) {
        for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
            //extractors can have prefixes, jus check if ends with
            if (!extractor.getName().endsWith(featureName)) {
                continue;
            }
            FrameVectorValues values = extractor.getOutputValues();
            Float fromIndex = (marker.getStart().floatValue() * values.getSampleRate()) / 1000;
            Float toIndex = fromIndex + (marker.getLength().floatValue() * values.getSampleRate()) / 1000;
            FrameVectorValues fvv = values.subList(fromIndex.intValue(), toIndex.intValue());
            log.debug("[findFeatureVectorValuesForMarker] feature[{2}] time: {0} == {1}",
                    marker.getLength(),
                    fvv.getTime(),
                    featureName
                    );
            return fvv;
        }
        return null;
    }

    public Map<String,IValues> findAllVectorValuesForMarker(IExtractorInputReader reader, Marker marker) {
        Map<String, IValues> result = new HashMap<String, IValues>();

        for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
            //extractors can have prefixes, jus check if ends with
            FrameVectorValues values = extractor.getOutputValues();
//            if(values.get(0).size()<=2){
//                continue;
//            }
            Float fromIndex = (marker.getStart().floatValue() * values.getSampleRate()) / 1000;
            fromIndex = fromIndex<0?0:fromIndex;
            Float toIndex = fromIndex + (marker.getLength().floatValue() * values.getSampleRate()) / 1000;
            FrameVectorValues fvv = values.subList(fromIndex.intValue(), toIndex.intValue());
            String key = extractor.getName().replace("BUFFERED_", "");
            result.put(key, fvv);
            log.debug("[findFeatureVectorValuesForMarker] feature[{2}] time: {0} == {1}",
                    marker.getLength(),
                    fvv.getTime()*1000,
                    key
            );
        }
        for (IExtractor extractor : reader.getExtractorRegister()) {
            //extractors can have prefixes, jus check if ends with
            FrameValues values = extractor.getOutputValues();
//            if(values.get(0).size()<=2){
//                continue;
//            }
            Float fromIndex = (marker.getStart().floatValue() * values.getSampleRate()) / 1000;
            fromIndex = fromIndex<0?0:fromIndex;
            Float toIndex = fromIndex + (marker.getLength().floatValue() * values.getSampleRate()) / 1000;
            FrameValues fv = values.subList(fromIndex.intValue(), toIndex.intValue());
            String key = extractor.getName().replace("BUFFERED_", "");
            result.put(key, fv);
            log.debug("[findFeatureVectorValuesForMarker] feature[{2}] time: {0} == {1}",
                    marker.getLength(),
                    fv.getTime()*1000,
                    key
            );
        }
        
        return result;
    }
}
