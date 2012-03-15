/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.ui.cmd;

import java.util.Map;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.IValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.services.impl.MatchingServiceImpl;

/**
 *
 * @author mondhs
 */
public class LearnCmd extends AbsrtactCmd {
    private MatchingServiceImpl matchingService;

    public LearnCmd(CommandExecutionFacade executionFacade) {
        super(executionFacade);
    }

    @Override
    public String execute(SpantusWorkInfo ctx) {
        getMatchingService().update(ctx.getProject().getRecognitionConfig());
        ExtractorReaderService extractorReaderService = WorkServiceFactory.createExtractorReaderService();

        Marker marker = ((Marker) getCurrentEvent().getValue());

        Map<String, IValues> fvv = extractorReaderService.findAllVectorValuesForMarker(
                getReader(),
                marker);
        
        AudioInputStream ais = null;
        try{
        	ais =
                AudioManagerFactory.createAudioManager().findInputStreamInMils(
                ctx.getProject().getSample().getCurrentFile(),
                marker.getStart(),
                marker.getLength());
        }catch( Exception e){
        	//not a audio
        }
        getMatchingService().learn(marker.getLabel(), fvv, ais);
        return null;
    }

    
    /**
     * 
     * @return
     */
    public Set<String> getExpectedActions() {
        return createExpectedActions(
                GlobalCommands.tool.learn.name());
    }

    public MatchingServiceImpl getMatchingService() {
        if(matchingService == null){
            matchingService = MatchingServiceImpl.getInstance();
        }
        return matchingService;
    }

    public void setMatchingService(MatchingServiceImpl matchingService) {
        this.matchingService = matchingService;
    }
   
    
    
}
