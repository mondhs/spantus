/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.externals.recognition.services;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.RecognitionResult;

/**
 *
 * @author mondhs
 */
public interface CorpusEntryExtractor {

    /**
     * Find segments(markers), then put them to corpus
     * @param filePath
     * @return
     */
    public List<CorpusEntry> extractInMemory(File filePath);
    /**
     * Extract sements and saves in dir. This will not hold markers in memory
     * @param filePath
     * @return - dir where it will save learned samples
     */
    public MarkerSetHolder extractAndLearn(File wavFilePath);
    /**
     * 
     * @param fileUrl
     * @param marker
     * @param reader
     * @return
     */
    public CorpusEntry learn(URL fileUrl, Marker marker, IExtractorInputReader reader);
    /**
     * 
     * @param marker
     * @param reader
     * @return
     */
	public RecognitionResult match(Marker marker, IExtractorInputReader reader);
	public Map<String, RecognitionResult> bestMatchesForFeatures(URL url, Marker marker,
			IExtractorInputReader reader);
			
	public  MarkerSet extractAndLearn(File filePath, MarkerSet segments,
			IExtractorInputReader reader);
	public MarkerSetHolder extract(File wavFilePath);
	public MarkerSetHolder extract(File wavFilePath, IExtractorInputReader reader);
//	public MarkerSet findSegementedLowestMarkers(MarkerSetHolder markerSetHolder);
//	public MarkerSet findSegementedHighestMarkers(MarkerSetHolder markerSetHolder);
	public IExtractorInputReader createReaderWithClassifier(File wavFilePath);
	/**
	 * Create labels from marker file
	 */
	public abstract String createLabel(File wavPath, Marker marker);
	/**
	 * Create labels from marker file
	 */
	public abstract String createLabelByMarkers(File filePath, Marker marker);

}
