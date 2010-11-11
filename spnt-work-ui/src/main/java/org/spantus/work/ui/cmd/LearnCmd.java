/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.work.ui.cmd;

import java.util.Map;
import java.util.Set;
import org.spantus.core.IValues;
import org.spantus.core.marker.Marker;
import org.spantus.externals.recognition.services.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.ui.dto.SpantusWorkInfo;

/**
 *
 * @author mondhs
 */
public class LearnCmd extends AbsrtactCmd{


    public LearnCmd(CommandExecutionFacade executionFacade) {
	super(executionFacade);
    }

    @Override
    public String execute(SpantusWorkInfo ctx) {
        CorpusService corpusService = RecognitionServiceFactory.createCorpusService();
        ExtractorReaderService extractorReaderService =  WorkServiceFactory.createExtractorReaderService();

        Marker marker =((Marker)getCurrentEvent().getValue());
        
        Map<String, IValues> fvv = extractorReaderService.findAllVectorValuesForMarker(getReader(),
                marker);
        
        corpusService.learn(marker.getLabel(), fvv);
        return null;
    }

    public Set<String> getExpectedActions() {
        return createExpectedActions(
				GlobalCommands.tool.learn.name()
				);
    }

}
