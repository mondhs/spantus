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
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.externals.recognition.ui.RecognizeDetailDialog;
import org.spantus.logger.Logger;
import org.spantus.utils.StringUtils;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;
import org.spantus.work.ui.services.impl.MatchingServiceImpl;
import scikit.util.Pair;

/**
 *
 * @author mondhs
 */
public class RecognizeCmd extends AbsrtactCmd {

    private static Logger log = Logger.getLogger(RecognizeCmd.class);
    private RecognizeDetailDialog info;
    private MatchingServiceImpl matchingService;
    private WorkExtractorReaderService extractorReaderService;

    public RecognizeCmd(CommandExecutionFacade executionFacade) {
        super(executionFacade);
    }

    @Override
    public String execute(SpantusWorkInfo ctx) {

        getMatchingService().update(ctx.getProject().getRecognitionConfig(), getExecutionFacade());

        Pair<MarkerSet,Marker> markerPair = ((Pair<MarkerSet,Marker>) getCurrentEvent().getValue());
        Marker marker = markerPair.snd();

        Map<String, IValues> fvv = getExtractorReaderService().recalcualteValues(getReader(), marker);

      

        List<RecognitionResult> results = getMatchingService().findMultipleMatch(
                markerPair.fst().getMarkerSetType(),
                fvv);
        if (results != null && results.size() > 0) {
            marker.setLabel(results.get(0).getInfo().getName());
        }
        info = null;
        getInfoPnl().setTargetWavURL(ctx.getProject().getSample().getCurrentFile());
        getInfoPnl().setTargetMarker(marker);
        getInfoPnl().updateCtx(results);
        getInfoPnl().setVisible(true);

        if (StringUtils.hasText(getInfoPnl().getSelectedSampleId())) {
            for (RecognitionResult result : results) {
                if (getInfoPnl().getSelectedSampleId().equals(result.getInfo().getId())) {
                    marker.setLabel(result.getInfo().getName());
                }

            }

        }
        log.debug("Recognize popup shown");
        return GlobalCommands.sample.reloadMarkers.name();
    }


    public Set<String> getExpectedActions() {
        return createExpectedActions(
                GlobalCommands.tool.recognize.name());
    }

    private RecognizeDetailDialog getInfoPnl() {
        if (info == null) {
            info = new RecognizeDetailDialog(null, I18nFactory.createI18n());
            info.setModal(true);
        }
        return info;
    }

    public WorkExtractorReaderService getExtractorReaderService() {
        if (extractorReaderService == null) {
            extractorReaderService = WorkServiceFactory.createExtractorReaderService();
        }
        return extractorReaderService;
    }

    public void setExtractorReaderService(WorkExtractorReaderService extractorReaderService) {
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
