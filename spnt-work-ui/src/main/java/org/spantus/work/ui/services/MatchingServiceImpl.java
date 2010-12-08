/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.work.ui.services;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import org.spantus.core.IValues;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.math.dtw.DtwServiceJavaMLImpl;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow;
import org.spantus.work.ui.dto.SpantusWorkInfo;

/**
 *
 * @author mondhs
 */
public class MatchingServiceImpl {
    private static MatchingServiceImpl matchingService;
    public static MatchingServiceImpl getInstance(){
        if(matchingService==null){
            matchingService = new MatchingServiceImpl();
        }
        return matchingService;
    }
    
    private CorpusServiceBaseImpl corpusService;
    private DtwServiceJavaMLImpl dtwService;
    private CorpusRepositoryFileImpl corpusRepo;

    
    /**
     * Update configuration
     * @param ctx
     */
    public void update(SpantusWorkInfo ctx) {
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
        getCorpusRepository().flush();
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
            corpusService.setIncludeFeatures(new HashSet<String>());
            corpusService.getIncludeFeatures().add(ExtractorEnum.MFCC_EXTRACTOR.name());
//            corpusService.getIncludeFeatures().add(ExtractorEnum.LPC_EXTRACTOR.name());
//            corpusService.getIncludeFeatures().add(ExtractorEnum.FFT_EXTRACTOR.name());
//            corpusServiceimpl.getIncludeFeatures().add(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name());

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
            CorpusRepositoryFileImpl corpusFileRepo = new CorpusRepositoryFileImpl();
            corpusRepo = corpusFileRepo;
        }
        return corpusRepo;
    }

    public void learn(String label, Map<String, IValues> fvv, AudioInputStream ais) {
        CorpusEntry corpusEntry = getCorpusService().create(label, fvv);
        getCorpusService().learn(corpusEntry, ais);
    }

    public List<RecognitionResultDetails> findMultipleMatch(Map<String, IValues> fvv) {
        return getCorpusService().findMultipleMatch(fvv);
    }

    public RecognitionResult match(Map<String, IValues> fvv) {
       return getCorpusService().match(fvv);
    }
}
