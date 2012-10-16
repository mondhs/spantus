package org.spantus.externals.recognition.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.IValues;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.IValueHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.RecognitionResultDetails;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.core.service.CorpusRepository;
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.logger.Logger;
import org.spantus.math.NumberUtils;
import org.spantus.math.dtw.DtwResult;
import org.spantus.math.dtw.DtwService;
import org.spantus.math.dtw.JavaMLLocalConstraint;
import org.spantus.math.dtw.JavaMLSearchWindow;
import org.spantus.math.services.MathServicesFactory;

/**
 *
 * @author Mindaugas Greibus
 *
 */
public class CorpusServiceBaseImpl implements CorpusService {

    private final static Logger LOG = Logger.getLogger(CorpusServiceBaseImpl.class);
    private DtwService dtwService;
    private CorpusRepository corpus;
    private Set<String> includeFeatures;
    private Float searchRadius;
    private CorpusServiceHelperImpl corpusServiceHelper;
    private Set<ProcessedFrameLinstener> listeners;
    private JavaMLSearchWindow javaMLSearchWindow;
    private JavaMLLocalConstraint javaMLLocalConstraint = JavaMLLocalConstraint.Default;

    @Override
    public RecognitionResult match(Map<String, IValues> target) {
        RecognitionResult match = findBestMatch(target);
        return match;
    }

    @Override
    public RecognitionResult matchByCorpusEntry(SignalSegment corpusEntry) {
        Map<String, IValues> target = getCorpusServiceHelper().toMap(corpusEntry);
        RecognitionResult match = findBestMatch(target);
        return match;
    }

    @Override
    public List<RecognitionResult> findMultipleMatchFull(SignalSegment corpusEntry) {
        Map<String, IValues> target = getCorpusServiceHelper().toMap(corpusEntry);
        List<RecognitionResult> match = findMultipleMatchFull(target);
        return match;
    }

    /**
     * Find mutliple match
     *
     * @param target
     * @return
     */
    @Override
    public List<RecognitionResult> findMultipleMatchFull(
            Map<String, IValues> target) {
        Long begin = System.currentTimeMillis();
        LOG.debug("[findMultipleMatch]+++ ");
        List<RecognitionResult> results = new ArrayList<RecognitionResult>();
        if (target == null || target.isEmpty()) {
            return results;
        }

        Long processedCount = 0L;
        Long totalToProcess = getCorpus().count() * target.size();
        started(totalToProcess);

        Map<String, Double> minimum = new HashMap<String, Double>();
        Map<String, Double> maximum = new HashMap<String, Double>();

        // iterate all entries in corpus
        for (SignalSegment corpusSample : findAll()) {
            LOG.debug("[findMultipleMatch] sample: {0} ",
                    corpusSample.getName());
            RecognitionResult result = getCorpusServiceHelper().createRecognitionResultDetail();
            Long targetLength = 0L;
            for ( IValues entry : target.values()) {
               if(entry!=null){
                   targetLength = entry.getTime();
                   //all length should be the same;
                   break;
               }
            }
            Long sampleLength = targetLength;
            if (corpusSample.getMarker() != null) {
                sampleLength = corpusSample.getMarker().getLength();
            }
            for (Map.Entry<String, IValues> targetEntry : target.entrySet()) {
                String featureName = targetEntry.getKey();

                IValueHolder<?> sampleFeatureData = corpusSample.getFeatureFrameVectorValuesMap().get(
                        targetEntry.getKey());

                if (sampleFeatureData == null) {
                    LOG.debug("[findMultipleMatch] sampleFeatureData is not found. skip for " + featureName);
                    continue;
                }

                DtwResult dtwResult = getCorpusServiceHelper().findDtwResult(featureName, targetEntry.getValue(), sampleFeatureData.getValues());
                result = updateResults(result, dtwResult, featureName, corpusSample, targetEntry);
                if (result != null) {
                    getCorpusServiceHelper().updateMinMax(featureName, dtwResult.getResult(), minimum,
                            maximum, sampleLength, targetLength);

                } else {
                    break;
                }
                processed(processedCount++, totalToProcess);
            }
            if (result != null) {
                RecognitionResultDetails details = result.getDetails();
                if(result.getInfo() != null){
                    SignalSegment resultInfo = result.getInfo();
                    String audioFile = getCorpus().findAudioFileById(resultInfo.getId());
                    details.setAudioFilePath(audioFile);
                    results.add(result);
                }else{
                    LOG.error("[findMultipleMatchFull]result.getInfo() is null ");
                }
            }
        }
        results = getCorpusServiceHelper().postProcessResult(results, minimum, maximum);
        LOG.debug("[findMultipleMatch]--- in {0} ms ",
                System.currentTimeMillis() - begin);
        ended();
        return results;
    }

    /**
     *
     * @param result
     * @param dtwResult
     * @param featureName
     * @param sample
     * @param targetEntry
     * @return
     */
    private RecognitionResult updateResults(
            RecognitionResult resultInfo, DtwResult dtwResult,
            String featureName, SignalSegment sample,
            Entry<String, IValues> targetEntry) {
        if (dtwResult == null || dtwResult.getResult().isInfinite()) {
            return null;
        }

        RecognitionResultDetails result = resultInfo.getDetails();
        IValueHolder<?> sampleFeatureData = sample
                .getFeatureFrameVectorValuesMap().get(targetEntry.getKey());
        result.getSampleLegths().put(
                featureName, sampleFeatureData.getValues().getTime());
        result.getTargetLegths().put(featureName, targetEntry.getValue().getTime());
        resultInfo.setInfo(sample);
        result.getPath().put(featureName, dtwResult.getPath());
        resultInfo.getScores().put(featureName, dtwResult.getResult());
        result.getCostMatrixMap().put(featureName, dtwResult.getCostMatrix());
        result.getStatisticalSummaryMap().put(featureName,
                dtwResult.getStatisticalSummary());
        return resultInfo;
    }

    /**
     * Same as {@link #learn(java.lang.String, java.util.Map)} only with audio
     * stream
     *
     * @param label
     * @param featureDataMap
     * @param audioStream
     * @return
     */
    @Override
    public SignalSegment learn(SignalSegment corpusEntry,
            AudioInputStream audioStream) {
        // CorpusEntry corpusEntry = create(label, featureDataMap);
        SignalSegment learnedCorpusEntry = getCorpus().update(corpusEntry,
                audioStream);
        return learnedCorpusEntry;
    }

    /**
     *
     * @param label
     * @param featureDataMap
     * @return
     */
    public SignalSegment create(String label, Map<String, IValues> featureDataMap) {
        return getCorpus().create(label, featureDataMap);
    }

    /**
     * learn with multiple features
     *
     * @param label
     * @param featureDataMap
     * @return
     */
    @Override
    public SignalSegment learn(String label, Map<String, IValues> featureDataMap) {
        SignalSegment corpusEntry = create(label, featureDataMap);
        return getCorpus().save(corpusEntry);
    }

    /**
     *
     * @return @param corpusEntry
     */
    @Override
    public Map<String, RecognitionResult> bestMatchesForFeatures(
            SignalSegment corpusEntry) {
        Map<String, IValues> target = new HashMap<String, IValues>();
        for (Entry<String, FrameVectorValuesHolder> featureData : corpusEntry.getFeatureFrameVectorValuesMap().entrySet()) {
            target.put(featureData.getKey(), featureData.getValue().getValues());
        }
        return bestMatchesForFeatures(target);
    }

    /**
     *
     */
    @Override
    public Map<String, RecognitionResult> bestMatchesForFeatures(
            Map<String, IValues> target) {
        Map<String, RecognitionResult> match = new HashMap<String, RecognitionResult>();
        for (SignalSegment corpusSample : findAll()) {
            long start = System.currentTimeMillis();
            for (Map.Entry<String, IValues> targetEntry : target.entrySet()) {
                String featureName = targetEntry.getKey();
                RecognitionResult result1 = getCorpusServiceHelper().compare(featureName,
                        targetEntry.getValue(), corpusSample);
                // match if this best for feature
                if (match.get(featureName) == null) {
                    // feature never seen
                    match.put(featureName, result1);
                } else {
                    Double prevDistance = match.get(featureName).getDistance();
                    if (result1 != null && NumberUtils
                            .compare(result1.getDistance(), prevDistance) < 0) {
                        match.put(featureName, result1);
                    }
                }
                LOG.debug(
                        "[bestMatchesForFeatures] iteration for [{1}] in {2} ms. score: {3} ",
                        corpusSample.getName(),
                        (System.currentTimeMillis() - start), match);
            }
        }
        return match;
    }

    /**
     * find best match in the corpus
     *
     * @param target
     * @return
     */
    protected RecognitionResult findBestMatch(Map<String, IValues> target) {
        List<RecognitionResult> results = new ArrayList<RecognitionResult>();
        Map<String, Double> minimum = new HashMap<String, Double>();
        Map<String, Double> maximum = new HashMap<String, Double>();
        int i = 1;
        Long processedCount = 0L;
        Long totalToProcess = getCorpus().count() * target.size();
        started(totalToProcess);

        for (SignalSegment corpusSample : findAll()) {
            long start = System.currentTimeMillis();
            Long targetLength = target.values().iterator().next().getTime();
            Long sampleLength = targetLength;
            if (corpusSample.getMarker() != null) {
                sampleLength = corpusSample.getMarker().getLength();
            }

            RecognitionResult result = new RecognitionResult();
            result.setScores(new HashMap<String, Double>());
            result.setInfo(corpusSample);
            //compare for each feature
            compareFeatures(target, corpusSample, result, minimum,
                    maximum, sampleLength, targetLength);
            processedCount += target.entrySet().size();
            processed(processedCount, totalToProcess);
            if (!result.getScores().isEmpty()) {
                results.add(result);
                LOG.debug(
                        "[findBestMatch] {0}. iteration for [{1}] in {2} ms.  ",
                        i++, corpusSample.getName(),
                        (System.currentTimeMillis() - start));
            }
            if (results.size() > 100) {
                results = getCorpusServiceHelper().postProcessResult(results, minimum, maximum);
            }
        }
        results = getCorpusServiceHelper().postProcessResult(results, minimum, maximum);
        LOG.info(MessageFormat.format("[findBestMatch] sample: {0}", results));
        if (results.isEmpty()) {
            return null;
        }
        ended();
        return results.get(0);
    }
    /**
     * 
     * @param target
     * @param corpusSample
     * @param result
     * @param minimum
     * @param maximum
     * @param sampleLength
     * @param targetLength 
     */
    private void compareFeatures(Map<String, IValues> target, SignalSegment corpusSample, RecognitionResult result, 
            Map<String, Double> minimum, Map<String, Double> maximum, Long sampleLength, Long targetLength) {
        for (Map.Entry<String, IValues> targetEntry : target.entrySet()) {
            String featureName = targetEntry.getKey();
            RecognitionResult result1 = getCorpusServiceHelper().compare(featureName,
                    targetEntry.getValue(), corpusSample);
            if (result1 == null) {
                LOG.debug("[findBestMatch]result not found");
                continue;
            }
            result.getScores().put(featureName, result1.getDistance());
            getCorpusServiceHelper().updateMinMax(featureName, result1.getDistance(), minimum,
                    maximum, sampleLength, targetLength);

        }
    }

    protected Iterable<SignalSegment> findAll() {
        return getCorpus().findAllEntries();
    }

    public void processed(Long current, Long total) {
        for (ProcessedFrameLinstener linstener : getListeners()) {
            linstener.processed(current, total);
        }
    }

    public void started(Long total) {
        for (ProcessedFrameLinstener linstener : getListeners()) {
            linstener.started(total);
        }
    }

    public void ended() {
        for (ProcessedFrameLinstener linstener : getListeners()) {
            linstener.ended();
        }
    }

    public void setCorpus(CorpusRepository corpus) {
        this.corpus = corpus;
    }

    public CorpusRepository getCorpus() {
        if (corpus == null) {
            corpus = new CorpusRepositoryFileImpl();
        }
        return corpus;
    }

    public DtwService getDtwService() {
        if (dtwService == null) {
            if (searchRadius == null || javaMLSearchWindow == null) {
                dtwService = MathServicesFactory.createDtwService();
            } else {
                dtwService = MathServicesFactory.createDtwService(getSearchRadius(), getJavaMLSearchWindow(), getJavaMLLocalConstraint());

            }
        }
        return dtwService;
    }

    public void setDtwService(DtwService dtwService) {
        this.dtwService = dtwService;
        corpusServiceHelper = null;
    }

    public Set<String> getIncludeFeatures() {
        return includeFeatures;
    }

    public void setIncludeFeatures(Set<String> includeFeatures) {
        corpusServiceHelper = null;
        this.includeFeatures = includeFeatures;
    }

    public Float getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(Float searchRadius) {
        this.searchRadius = searchRadius;
        dtwService = null;
        corpusServiceHelper = null;
    }

    public JavaMLSearchWindow getJavaMLSearchWindow() {
        return javaMLSearchWindow;
    }

    public void setJavaMLSearchWindow(JavaMLSearchWindow javaMLSearchWindow) {
        this.javaMLSearchWindow = javaMLSearchWindow;
        dtwService = null;
        corpusServiceHelper = null;
    }

    public JavaMLLocalConstraint getJavaMLLocalConstraint() {
        return javaMLLocalConstraint;
    }

    public void setJavaMLLocalConstraint(JavaMLLocalConstraint javaMLLocalConstraint) {
        this.javaMLLocalConstraint = javaMLLocalConstraint;
    }

    public Set<ProcessedFrameLinstener> getListeners() {
        if (listeners == null) {
            listeners = new LinkedHashSet<ProcessedFrameLinstener>();
        }
        return listeners;
    }

    public CorpusServiceHelperImpl getCorpusServiceHelper() {
        if (corpusServiceHelper == null) {
            corpusServiceHelper = new CorpusServiceHelperImpl();
            corpusServiceHelper.setIncludeFeatures(getIncludeFeatures());
            corpusServiceHelper.setDtwService(getDtwService());
        }
        return corpusServiceHelper;
    }

    public void setCorpusServiceHelper(CorpusServiceHelperImpl corpusServiceHelper) {
        this.corpusServiceHelper = corpusServiceHelper;
    }
}
