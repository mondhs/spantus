/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.services;

import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import org.spantus.core.IValues;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.marker.Marker;
import org.spantus.core.service.ExtractorInputReaderService;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.extractor.impl.ExtractorEnum;

/**
 *
 * @author mondhs
 */
public interface WorkExtractorReaderService extends ExtractorInputReaderService {
    /**
     * 
     * @param extractors
     * @param url
     * @return 
     */
    public IExtractorInputReader createReader(ExtractorEnum[] extractors, URL url);
    /**
     * Read given signal file extracts feature data
     * @param extractors
     * @param file
     * @return
     */
    public IExtractorInputReader createReader(ExtractorEnum[] extractors, File file);
    
    public IExtractorInputReader createReader(ExtractorEnum[] extractors, AudioInputStream ais);
    /**
     * Read given signal file extracts feature with clasifiers{@link IClassifier} data
     * @param extractors
     * @param file
     * @return
     */
    public IExtractorInputReader createReaderWithClassifier(ExtractorEnum[] extractors, File file);
    
    public IExtractorInputReader createReaderWithClassifier(ExtractorEnum[] extractors, File file, Map<String, ExtractorParam> params);
    
    public IExtractorInputReader createReaderWithClassifier(ExtractorEnum[] extractors, File file, Map<String, ExtractorParam> params, ClassifierEnum classifier);
    

    /**
     * Read given signal file extracts feature date and save it to disk
     * @param extractors
     * @param file
     * @return
     */
    public IExtractorInputReader createReaderAndSave(ExtractorEnum[] extractors, File file);

    /**
     * Find extractor by name in {@link IExtractorInputReader}
     * @param name
     * @param reader
     * @return
     */
    public IGeneralExtractor<?> findExtractorByName(String name, IExtractorInputReader reader);
    
    public Map<String, IValues> recalcualteValues(IExtractorInputReader extractorInputReader, Marker marker);
    public Map<String, IValues> recalcualteValues(IExtractorInputReader extractorInputReader, Marker marker, String extractorName);
}
