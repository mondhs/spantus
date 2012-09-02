/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.ui.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.IValues;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.exception.ProcessingException;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLLocalConstraint;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow;
import org.spantus.math.services.MathServicesFactory;
import org.spantus.utils.StringUtils;
import org.spantus.work.ui.dto.RecognitionConfig;
import scikit.util.Pair;

/**
 *
 * @author mondhs
 */
public class MatchingServiceImpl {

    private static MatchingServiceImpl matchingService;

    public static MatchingServiceImpl getInstance() {
        if (matchingService == null) {
            matchingService = new MatchingServiceImpl();
        }
        return matchingService;
    }
    private Map<String, CorpusServiceBaseImpl> corpusServiceMap = new HashMap<String, CorpusServiceBaseImpl>();
    private Map<String, CorpusRepositoryFileImpl> corpusRepoMap = new HashMap<String, CorpusRepositoryFileImpl>();
    private CorpusServiceBaseImpl defaultCorpusService;
    private CorpusRepositoryFileImpl defaultCorpusRepo;
    private Long lastChangedFile = 0L;
    
    /**
     * Update configuration
     *
     * @param ctx
     */
    public void update(RecognitionConfig recognitionConfig, ProcessedFrameLinstener listener) {

        String corpusPath = recognitionConfig.getRepositoryPath();
        String searchWindowStr = recognitionConfig.getDtwWindow();
        String localConstraintStr = recognitionConfig.getLocalConstraint();
        JavaMLSearchWindow searchWindow = JavaMLSearchWindow.FullWindow;
        if (StringUtils.hasText(searchWindowStr)) {
            searchWindow = JavaMLSearchWindow
                    .valueOf(searchWindowStr);
        }
        JavaMLLocalConstraint localConstraint = JavaMLLocalConstraint.Default;
        if (StringUtils.hasText(localConstraintStr)) {
            localConstraint = JavaMLLocalConstraint.valueOf(localConstraintStr);
        }
        Float radius = recognitionConfig.getRadius();


        File corpusDir = new File(corpusPath);

        if (corpusRepoMap.isEmpty() && defaultCorpusRepo == null) {
            createRecognitionServices(corpusDir, radius, searchWindow, localConstraint, listener);
        }
        updateRecognitionServices(corpusDir, radius, searchWindow, localConstraint, listener);


        // corpusService.getIncludeFeatures().add(ExtractorEnum.FFT_EXTRACTOR.name());
        // corpusServiceimpl.getIncludeFeatures().add(ExtrasSctorEnum.SPECTRAL_FLUX_EXTRACTOR.name());

    }
    /**
     * 
     * @param theCorpusDir
     * @param radius
     * @param searchWindow
     * @param localConstraint
     * @param listener 
     */
    public void updateRecognitionServices(File theCorpusDir, Float radius, JavaMLSearchWindow searchWindow, JavaMLLocalConstraint localConstraint, ProcessedFrameLinstener listener) {
        Pair<Long, List<File>> subCorpusPair = findSubCorpus(theCorpusDir);
        List<File> subCorpus = subCorpusPair.snd();
        Long maxChangedFile = subCorpusPair.fst();
        boolean insync = maxChangedFile != lastChangedFile;
        //default exists but there is sucorpuses
        if(!insync){
            //do nothing
        }else if (defaultCorpusRepo != null && !subCorpus.isEmpty()) {
            insync = false;
        } else if (defaultCorpusRepo != null && subCorpus.isEmpty()) {
            if (!defaultCorpusRepo.getRepoDir().equals(theCorpusDir)) {
                defaultCorpusRepo.setRepositoryPath(theCorpusDir.getAbsolutePath());
                defaultCorpusRepo.flush();
            }
            defaultCorpusService.setDtwService(MathServicesFactory.createDtwService(
                radius, searchWindow, localConstraint));
        } else if (!getCorpusRepoMap().isEmpty() && getCorpusRepoMap().size() == subCorpus.size()) {
            for (File iCorpusPath : subCorpus) {
                String corpusKey = iCorpusPath.getName();
                CorpusRepositoryFileImpl aCorpusRepo = getCorpusRepoMap().get(corpusKey);
                if (aCorpusRepo == null) {
                    //not sync clear up;
                    insync = false;
                    break;
                }
                if (!aCorpusRepo.getRepoDir().equals(iCorpusPath)) {
                    aCorpusRepo.setRepositoryPath(iCorpusPath.getAbsolutePath());
                    aCorpusRepo.flush();
                }
                getCorpusServiceMap().get(corpusKey).setDtwService(MathServicesFactory.createDtwService(
                radius, searchWindow, localConstraint));
            }
        }else{
            insync = false;
        }
        if (!insync) {
            createRecognitionServices(theCorpusDir, radius, searchWindow, localConstraint, listener);
        }
    }

    public void createRecognitionServices(File theCorpusDir, Float radius, JavaMLSearchWindow searchWindow, JavaMLLocalConstraint localConstraint, ProcessedFrameLinstener listener) {
        defaultCorpusRepo = null;
        defaultCorpusService = null;
        getCorpusRepoMap().clear();
        Pair<Long, List<File>> subCorpusPair = findSubCorpus(theCorpusDir);
        List<File> subCorpus = subCorpusPair.snd();
        lastChangedFile = subCorpusPair.fst();
        if (subCorpus.isEmpty()) {
            getCorpusRepoMap().clear();
            CorpusRepositoryFileImpl aCorpusFileRepo = createCorpusRepo(theCorpusDir);
            CorpusServiceBaseImpl aService = createCorpusService(
                    radius, searchWindow, localConstraint, listener);
            aService.setCorpus(aCorpusFileRepo);
            defaultCorpusService = aService;
            defaultCorpusRepo = aCorpusFileRepo;
        } else {
            getCorpusRepoMap().clear();
            for (File iCorpusPath : subCorpus) {
                String corpusKey = iCorpusPath.getName();
                CorpusRepositoryFileImpl aCorpusFileRepo = createCorpusRepo(iCorpusPath);
                CorpusServiceBaseImpl aService = createCorpusService(
                        radius, searchWindow, localConstraint, listener);
                aService.setCorpus(aCorpusFileRepo);
                getCorpusServiceMap().put(corpusKey, aService);
                getCorpusRepoMap().put(corpusKey, aCorpusFileRepo);
            }
        }
    }
    /**
     * 
     * @param iCorpusPath
     * @return 
     */
    private CorpusRepositoryFileImpl createCorpusRepo(File iCorpusPath) {
        CorpusRepositoryFileImpl aCorpusRepo = new CorpusRepositoryFileImpl();
        aCorpusRepo.setRepositoryPath(iCorpusPath.getAbsolutePath());
        aCorpusRepo.flush();
        return aCorpusRepo;
    }

    /**
     *
     * @param radius
     * @param searchWindow
     * @param localConstraint
     * @param listener
     * @return
     */
    private CorpusServiceBaseImpl createCorpusService(Float radius, JavaMLSearchWindow searchWindow, JavaMLLocalConstraint localConstraint, ProcessedFrameLinstener listener) {
        ExtractorEnum[] includedExtractorArr = new ExtractorEnum[]{ExtractorEnum.MFCC_EXTRACTOR,
            ExtractorEnum.PLP_EXTRACTOR,
            ExtractorEnum.LPC_EXTRACTOR};
        CorpusServiceBaseImpl aCorpusService = new CorpusServiceBaseImpl();
        aCorpusService.setDtwService(MathServicesFactory.createDtwService(
                radius, searchWindow, localConstraint));
        aCorpusService.setIncludeFeatures(new HashSet<String>());
        for (ExtractorEnum includeExtr : includedExtractorArr) {
            aCorpusService.getIncludeFeatures().add(includeExtr.name());
        }
        aCorpusService.getListeners().add(listener);
        return aCorpusService;
    }
    /**
     * 
     * @param corpusDir
     * @return 
     */
    private Pair<Long,List<File>> findSubCorpus(File corpusDir) {
        List<File> subCorpus = new ArrayList<File>();
        Long maxChangedFile = 0L;
        if(!corpusDir.exists()){
            throw new ProcessingException("Please, in Tool>Option>Recognition>Corpus Path as current directory does not exists: " + corpusDir);
        }
        for (File iDir : corpusDir.listFiles()) {
            if (iDir.isDirectory()) {
                subCorpus.add(iDir);
                for (File iFile : iDir.listFiles()) {
                    maxChangedFile = Math.max(maxChangedFile, iFile.lastModified());
                }
            }
            maxChangedFile = Math.max(maxChangedFile, iDir.lastModified());
        }
        return new Pair<Long,List<File>>(maxChangedFile, subCorpus);
    }

    private CorpusServiceBaseImpl resolveCorpusService(String markerType){
        CorpusServiceBaseImpl aCorpusService = defaultCorpusService;
        if(aCorpusService == null){
            aCorpusService = getCorpusServiceMap().get(markerType);
        }
        if(aCorpusService == null){
            throw new ProcessingException("Corpus service not found for " + markerType);
        }
        return aCorpusService;
    }
    
    public void learn(String markerType, String label, Map<String, IValues> fvv,
            AudioInputStream ais) {
        CorpusServiceBaseImpl aCorpusService = resolveCorpusService(markerType);
        SignalSegment corpusEntry = aCorpusService.create(label, fvv);
        aCorpusService.learn(corpusEntry, ais);
    }

    public List<RecognitionResult> findMultipleMatch(String markerType,
            Map<String, IValues> fvv) {
        CorpusServiceBaseImpl aCorpusService = resolveCorpusService(markerType);
        return aCorpusService.findMultipleMatchFull(fvv);
    }

    public RecognitionResult match(String markerType, Map<String, IValues> fvv) {
        CorpusServiceBaseImpl aCorpusService = resolveCorpusService(markerType);
        RecognitionResult result = aCorpusService.match(fvv);
//		Double mfccScore = result.getScores().get("MFCC_EXTRACTOR");
//		Double plpScore = result.getScores().get("PLP_EXTRACTOR");
//		if (mfccScore != null && mfccScore > 250) {
//			return null;
//		}
//		if (plpScore != null && plpScore > 15) {
//			return null;
//		}
        return result;
    }

    public Map<String, CorpusServiceBaseImpl> getCorpusServiceMap() {
        return corpusServiceMap;
    }

    public void setCorpusServiceMap(Map<String, CorpusServiceBaseImpl> corpusServiceMap) {
        this.corpusServiceMap = corpusServiceMap;
    }

    public CorpusServiceBaseImpl getDefaultCorpusService() {
        return defaultCorpusService;
    }

    public void setDefaultCorpusService(CorpusServiceBaseImpl defaultCorpusService) {
        this.defaultCorpusService = defaultCorpusService;
    }

    public CorpusRepositoryFileImpl getDefaultCorpusRepository() {
        return defaultCorpusRepo;
    }

    public Map<String, CorpusRepositoryFileImpl> getCorpusRepoMap() {
        return corpusRepoMap;
    }

    public CorpusRepositoryFileImpl getDefaultCorpusRepo() {
        return defaultCorpusRepo;
    }
}
