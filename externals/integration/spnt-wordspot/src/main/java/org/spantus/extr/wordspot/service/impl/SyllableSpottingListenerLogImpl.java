package org.spantus.extr.wordspot.service.impl;

import java.util.List;

import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;

/**
 *
 * @author Mindaugas Greibus
 * @since 0.3 Created: May 7, 2012
 *
 */
public class SyllableSpottingListenerLogImpl extends WordSpottingListenerLogImpl {

	/**
	 * 
	 * @param target
	 * @param acceptableSyllable
	 * @param repositoryPathSyllable
	 */
    public SyllableSpottingListenerLogImpl(String target,
			String[] acceptableSyllable, String repositoryPathSyllable) {
    	//String theRepositoryPathWord = repositoryPathSyllable;
		super(target, acceptableSyllable, repositoryPathSyllable);
	}

	/**
     * 
     * @param sourceId
     * @param newSyllable
     * @param recognitionResults
     * @return 
     */
    @Override
    public String foundSegment(String sourceId, SignalSegment newSyllable, List<RecognitionResult> recognitionResults) {
    	String segmentName = super.foundSegment(sourceId, newSyllable, recognitionResults);
        return segmentName;
    }
    
    @Override
    protected RecognitionResult matchAndRegistry(
    		List<SignalSegment> aSignalSegmentsSyllable,
    		SignalSegment newSyllable,
    		RecognitionResult syllableRecognitionResult) {
    	getWordSegments().put(syllableRecognitionResult, newSyllable);
    	return syllableRecognitionResult;
    }
    
    
    

    
    
}
