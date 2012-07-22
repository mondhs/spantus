package org.spantus.extr.wordspot.service.impl;

import java.util.List;

import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.extr.wordspot.service.WordSpottingListener;
import org.spantus.logger.Logger;

/**
 *
 * @author Mindaugas Greibus
 * @since 0.3 Created: May 7, 2012
 *
 */
public class SpottingMarkerSegmentatorListenerImpl extends RecognitionMarkerSegmentatorListenerImpl {
    
    private static final Logger LOG = Logger.getLogger(SpottingMarkerSegmentatorListenerImpl.class);    
    private WordSpottingListener wordSpottingListener;
    
    public SpottingMarkerSegmentatorListenerImpl(WordSpottingListener wordSpottingListener) {
        this.wordSpottingListener = wordSpottingListener;
    }
    
    @Override
    protected boolean processEndedSegment(SignalSegment signalSegment) {
        super.processEndedSegment(signalSegment);
        //done in purpose
        return false;
    }
    
    @Override
    protected String match(SignalSegment signalSegment) {
        List<RecognitionResult> result = getCorpusService().findMultipleMatchFull(signalSegment.findAllFeatures());
        LOG.debug("[match] segment for mathcing: {0}", signalSegment.getMarker());
        for (RecognitionResult recognitionResult : result) {
            LOG.debug("[match] matched segment: {0} [{1}]", recognitionResult.getInfo().getName(), recognitionResult.getScores());
        }
//        RecognitionResult result1 = getCorpusService().matchByCorpusEntry(signalSegment);
        wordSpottingListener.foundSegment(null, signalSegment, result);
        return null;
    }
    
    public WordSpottingListener getWordSpottingListener() {
        return wordSpottingListener;
    }
    
    public void setWordSpottingListener(WordSpottingListener wordSpottingListener) {
        this.wordSpottingListener = wordSpottingListener;
    }
}
