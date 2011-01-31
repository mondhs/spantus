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
package org.spantus.work.ui.cmd;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spantus.core.IValues;
import org.spantus.core.marker.Marker;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;
import org.spantus.externals.recognition.ui.RecognizeDetailDialog;
import org.spantus.logger.Logger;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;
import org.spantus.work.ui.services.impl.MatchingServiceImpl;

/**
 *
 * @author mondhs
 */
public class RecognizeCmd extends AbsrtactCmd {

    private static Logger log = Logger.getLogger(RecognizeCmd.class);
    private RecognizeDetailDialog info;
    private MatchingServiceImpl matchingService;
    private ExtractorReaderService extractorReaderService;

    
    
    public RecognizeCmd(CommandExecutionFacade executionFacade) {
        super(executionFacade);
    }
    
    @Override
    public String execute(SpantusWorkInfo ctx) {
        
        getMatchingService().update(ctx.getProject().getRecognitionConfig()); 
        
        Marker marker = ((Marker) getCurrentEvent().getValue());

        Map<String, IValues> fvv = getExtractorReaderService().findAllVectorValuesForMarker(
                getReader(),
                marker);

        List<RecognitionResultDetails> results = getMatchingService().findMultipleMatch(
                fvv);
        if(results!=null && results.size()>0){
            marker.setLabel(results.get(0).getInfo().getName());
        }
        info = null;
        getInfoPnl().setTargetWavURL(ctx.getProject().getSample().getCurrentFile());
        getInfoPnl().setTargetMarker(marker);
        getInfoPnl().updateCtx(results);
        getInfoPnl().setVisible(true);
        log.debug("Recognize popup shown");
        return GlobalCommands.sample.reloadMarkers.name();
    }

    public Set<String> getExpectedActions() {
        return createExpectedActions(
                GlobalCommands.tool.recognize.name());
    }
    
    private RecognizeDetailDialog getInfoPnl() {
        if (info == null) {
            info = new RecognizeDetailDialog(null,I18nFactory.createI18n());
            info.setModal(true);
        }
        return info;
    }

    public ExtractorReaderService getExtractorReaderService() {
        if (extractorReaderService == null) {
            extractorReaderService = WorkServiceFactory.createExtractorReaderService();
        }
        return extractorReaderService;
    }

    public void setExtractorReaderService(ExtractorReaderService extractorReaderService) {
        this.extractorReaderService = extractorReaderService;
    }

    public MatchingServiceImpl getMatchingService() {
        if (matchingService == null) {
            matchingService = MatchingServiceImpl.getInstance();
        }
        return matchingService;
    }

    public void setMatchingService(MatchingServiceImpl matchingService) {
        this.matchingService = matchingService;
    }
}
