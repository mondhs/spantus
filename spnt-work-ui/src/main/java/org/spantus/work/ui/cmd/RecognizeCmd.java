/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.ui.cmd;

import java.util.List;
import java.util.Set;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.marker.Marker;
import org.spantus.externals.recognition.bean.FeatureData;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.services.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.ui.container.panel.RecognizeDetailDialog;
import org.spantus.work.ui.dto.SpantusWorkInfo;

/**
 *
 * @author mondhs
 */
public class RecognizeCmd extends AbsrtactCmd {

    private static Logger log = Logger.getLogger(RecognizeCmd.class);
    private RecognizeDetailDialog info;

    public RecognizeCmd(CommandExecutionFacade executionFacade) {
        super(executionFacade);
    }

    @Override
    public String execute(SpantusWorkInfo ctx) {
        CorpusService corpusService = RecognitionServiceFactory.createCorpusService();
        ExtractorReaderService extractorReaderService = WorkServiceFactory.createExtractorReaderService();

        Marker marker = ((Marker) getCurrentEvent().getValue());

        FrameVectorValues fvv = extractorReaderService.findFeatureVectorValuesForMarker(getReader(),
                marker, ExtractorEnum.MFCC_EXTRACTOR.name());

        FeatureData fd = new FeatureData();
        fd.setValues(fvv);
        List<RecognitionResult> results = corpusService.multipleMatch(fd);
        getInfoPnl().updateCtx(ctx,results);
	getInfoPnl().setVisible(true);
        return null;
    }

    public Set<String> getExpectedActions() {
        return createExpectedActions(
                GlobalCommands.tool.recognize.name());
    }

    private RecognizeDetailDialog getInfoPnl() {
        if (info == null) {
            info = new RecognizeDetailDialog(null);

            info.setModal(true);
        }
        return info;
    }
}
