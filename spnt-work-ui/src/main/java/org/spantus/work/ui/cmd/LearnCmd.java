/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.ui.cmd;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.sound.sampled.AudioInputStream;
import org.spantus.core.IValues;
import org.spantus.core.marker.Marker;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.math.dtw.DtwServiceJavaMLImpl;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow;

/**
 *
 * @author mondhs
 */
public class LearnCmd extends AbsrtactCmd {

    private CorpusServiceBaseImpl corpusService;
    private DtwServiceJavaMLImpl dtwService;
    private CorpusRepositoryFileImpl corpusRepo;

    public LearnCmd(CommandExecutionFacade executionFacade) {
        super(executionFacade);
    }

    @Override
    public String execute(SpantusWorkInfo ctx) {
        update(ctx);
        ExtractorReaderService extractorReaderService = WorkServiceFactory.createExtractorReaderService();

        Marker marker = ((Marker) getCurrentEvent().getValue());

        Map<String, IValues> fvv = extractorReaderService.findAllVectorValuesForMarker(
                getReader(),
                marker);
        AudioInputStream ais =
                AudioManagerFactory.createAudioManager().findInputStreamInMils(
                ctx.getProject().getSample().getCurrentFile(),
                marker.getStart(),
                marker.getLength());

        getCorpusService().learn(marker.getLabel(), fvv, ais);
        return null;
    }

    /**
     * Update configuration
     * @param ctx
     */
    protected void update(SpantusWorkInfo ctx) {
        String corpusPath = ctx.getProject().getRecognitionConfig().getRepositoryPath();
        String searchWindowStr = ctx.getProject().getRecognitionConfig().getDtwWindow();
        JavaMLSearchWindow searchWindow = JavaMLSearchWindow.valueOf(searchWindowStr);
        int radius = ctx.getProject().getRecognitionConfig().getRadius();

        File corpusDir = new File(corpusPath);
        if (!corpusDir.equals(getCorpusRepository().getRepoDir())) {
            getCorpusRepository().setRepositoryPath(corpusPath);
            getCorpusRepository().flush();
        }
        if (searchWindow != null) {
            getDtwService().setSearchWindow(searchWindow);
        }
        getDtwService().setSearchRadius(radius);
    }
    /**
     * 
     * @return
     */
    public Set<String> getExpectedActions() {
        return createExpectedActions(
                GlobalCommands.tool.learn.name());
    }
    /**
     * 
     * @return
     */
    public CorpusServiceBaseImpl getCorpusService() {
        if (corpusService == null) {
            corpusService = new CorpusServiceBaseImpl();
            corpusService.setDtwService(getDtwService());
            corpusService.setCorpus(getCorpusRepository());
//            corpusService = RecognitionServiceFactory.createCorpusService();
        }
        return corpusService;
    }

    public DtwServiceJavaMLImpl getDtwService() {
        if (dtwService == null) {
            dtwService = new DtwServiceJavaMLImpl();
        }
        return dtwService;
    }

    public void setCorpusService(CorpusServiceBaseImpl corpusService) {
        this.corpusService = corpusService;
    }

    public CorpusRepositoryFileImpl getCorpusRepository() {
        if (corpusRepo == null) {
            CorpusRepositoryFileImpl corpusRepo = new CorpusRepositoryFileImpl();
        }
        return corpusRepo;
    }
}
