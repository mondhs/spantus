/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.ui.cmd;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.marker.Marker;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;
import org.spantus.externals.recognition.services.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.logger.Logger;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.externals.recognition.ui.RecognizeDetailDialog;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;

/**
 *
 * @author mondhs
 */
public class RecognizeCmd extends AbsrtactCmd {

    private static Logger log = Logger.getLogger(RecognizeCmd.class);
    private RecognizeDetailDialog info;
    private CorpusService corpusService;
    private ExtractorReaderService extractorReaderService;

    public RecognizeCmd(CommandExecutionFacade executionFacade) {
        super(executionFacade);
    }

    @Override
    public String execute(SpantusWorkInfo ctx) {

        Marker marker = ((Marker) getCurrentEvent().getValue());

        Map<String, FrameVectorValues> fvv = getExtractorReaderService().findAllVectorValuesForMarker(getReader(),
                marker);

        List<RecognitionResultDetails> results = getCorpusService().findMultipleMatch(fvv);
        if(results!=null && results.size()>0){
            marker.setLabel(results.get(0).getInfo().getName());
        }
        info = null;
        getInfoPnl().updateCtx(results);
	getInfoPnl().setVisible(true);
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
    public CorpusService getCorpusService() {
        if(corpusService == null){
            corpusService = RecognitionServiceFactory.createCorpusService();
        }
        return corpusService;
    }

    public void setCorpusService(CorpusService corpusService) {
        this.corpusService = corpusService;
    }

    public ExtractorReaderService getExtractorReaderService() {
        if(extractorReaderService == null){
            extractorReaderService = WorkServiceFactory.createExtractorReaderService();
        }
        return extractorReaderService;
    }

    public void setExtractorReaderService(ExtractorReaderService extractorReaderService) {
        this.extractorReaderService = extractorReaderService;
    }


}
