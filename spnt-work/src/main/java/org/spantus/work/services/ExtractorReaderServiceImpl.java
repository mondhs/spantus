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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.io.AudioReader;
import org.spantus.core.io.AudioReaderFactory;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
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
            return fvv;
        }
        return null;
    }

    public Map<String, IValues> findAllVectorValuesForMarker(IExtractorInputReader reader, Marker marker) {
        Map<String, IValues> result = new HashMap<String, IValues>();

        for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
            //extractors can have prefixes, jus check if ends with
            FrameVectorValues values = extractor.getOutputValues();
            int endIndex = values.size()-1;
//            if(values.get(0).size()<=2){
//                continue;
//            }
            Float fromIndex = (marker.getStart().floatValue() * values.getSampleRate()) / 1000;
            fromIndex = fromIndex < 0 ? 0 : fromIndex;
            Float toIndex = fromIndex + (marker.getLength().floatValue() * values.getSampleRate()) / 1000;
            toIndex = endIndex < toIndex?endIndex:toIndex;
            FrameVectorValues fvv = values.subList(fromIndex.intValue(), toIndex.intValue());
            String key = extractor.getName().replace("BUFFERED_", "");
            result.put(key, fvv);
        }
        for (IExtractor extractor : reader.getExtractorRegister()) {
        	if(extractor.getName().endsWith(ExtractorEnum.SIGNAL_EXTRACTOR.name())){
        		continue;
        	}
            //extractors can have prefixes, just check if ends with
            FrameValues values = extractor.getOutputValues();
            int endIndex = values.size()-1;
            Float fromIndex = (marker.getStart().floatValue() * values.getSampleRate()) / 1000;
            fromIndex = fromIndex < 0 ? 0 : fromIndex;
            Float toIndex = fromIndex + (marker.getLength().floatValue() * values.getSampleRate()) / 1000;
            toIndex = endIndex < toIndex?endIndex:toIndex;
            FrameValues fv = values.subList(fromIndex.intValue(), toIndex.intValue());
            String key = extractor.getName().replace("BUFFERED_", "");
            result.put(key, fv);
        }

        return result;
    }
    /**
     * 
     * @param extractors
     * @param inputFile
     * @return
     */
    public IExtractorInputReader createReaderWithClassifier(ExtractorEnum[] extractors, 
            File inputFile) {
        URL inputUrl;
        try {
            inputUrl = inputFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        AudioReader audioReader = AudioReaderFactory.createAudioReader();
        IExtractorInputReader extractorReader = ExtractorsFactory.createReader(
                audioReader.getAudioFormat(inputUrl));
        log.debug("[createReaderWithClassifier] reader config{0}", extractorReader.getConfig() );
        ExtractorUtils.
                registerThreshold(extractorReader, extractors, null, ClassifierEnum.rules);
//                registerThreshold(extractorReader, extractors, null);

        log.debug("[createReaderWithClassifier] reader features{0}", extractorReader.getGeneralExtractor() );

        audioReader.readSignal(inputUrl, extractorReader);

        return extractorReader;
    }
    
    
    
    /**
     * 
     * @param extractors
     * @param inputFile
     * @return
     */
    public IExtractorInputReader createReader(ExtractorEnum[] extractors,
            File inputFile) {
        URL inputUrl;
        try {
            inputUrl = inputFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        AudioReader audioReader = AudioReaderFactory.createAudioReader();
        IExtractorInputReader extractorReader = ExtractorsFactory.createReader(
                audioReader.getAudioFormat(inputUrl));
        ExtractorUtils.register(extractorReader, extractors, null);
        audioReader.readSignal(inputUrl, extractorReader);

        return extractorReader;
    }

    
    
    /**
     * 
     * @param extractors
     * @param inputFile
     * @return
     */
    public IExtractorInputReader createReaderAndSave(ExtractorEnum[] extractors,
            File inputFile) {
        
        IExtractorInputReader extractorReader = createReader(extractors, inputFile);
        //save
        WorkServiceFactory.createReaderDao().write(extractorReader, createExtactorFile(inputFile));
        return extractorReader;
    }

    /**
     * 
     * @param name
     * @param reader
     * @return
     */
    public IGeneralExtractor findExtractorByName(String name, IExtractorInputReader reader) {
        for (IExtractor extractor : reader.getExtractorRegister()) {
            if (extractor.getName().contains(name)) {
                return extractor;
            }
        }
        for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
            if (extractor.getName().contains(name)) {
                return extractor;
            }
        }
        return null;
    }

    protected File createExtactorFile(File wavFile) {
        File newFile = new File(wavFile.getAbsoluteFile().toString() + ".sspnt.xml");
        return newFile;
    }
}
