/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.impl;

import java.util.List;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.logger.Logger;

/**
 *
 * @author as
 */
public class SpottingListenerLogger implements SpottingListener{
    private static final Logger LOG = Logger.getLogger(SpottingListenerLogger.class); 
    
    @Override
    public String foundSegment(String sourceId, SignalSegment newSegment, List<RecognitionResult> recognitionResults) {
        LOG.debug("foundSegment newSegment: {0}", newSegment);
        return newSegment.getMarker().getLabel();
    }
    
}
