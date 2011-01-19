/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorFileImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;

/**
 *
 * @author mondhs
 */
public abstract class AbstractSegmentDirTest {

    public final static String DIR_LEARN_WAV =
            "/mnt/audio/MG" //            "/home/mondhs/src/garsynai/skaiciai/learn"
            ;
    public final static String DIR_LEARN =
            "/mnt/audio/MG/OUTPUT/"
//            "./target/learn-corpus/"
            ;
    private File learnDir;
    private CorpusEntryExtractorFileImpl extractor;
    private CorpusServiceBaseImpl corpusService;
    private CorpusRepositoryFileImpl corpusRepository;
    private static final Logger log = Logger.getLogger(AbstractSegmentDirTest.class);

    @Before
    public void onSetup() {
        learnDir = new File(DIR_LEARN_WAV);
        extractor = new CorpusEntryExtractorFileImpl();
        corpusService = new CorpusServiceBaseImpl();
        corpusRepository = new CorpusRepositoryFileImpl();
        corpusRepository.setRepositoryPath(DIR_LEARN);
        corpusService.setCorpus(corpusRepository);
        extractor.setCorpusService(corpusService);

        OnlineDecisionSegmentatorParam segmentionParam = new OnlineDecisionSegmentatorParam();
        segmentionParam.setMinLength(91L);
        segmentionParam.setMinSpace(261L);
        segmentionParam.setExpandStart(260L);
        segmentionParam.setExpandEnd(360L);

        ExtractorEnum[] extractors = new ExtractorEnum[]{
            ExtractorEnum.MFCC_EXTRACTOR,
            ExtractorEnum.PLP_EXTRACTOR,
            ExtractorEnum.LPC_EXTRACTOR,
            ExtractorEnum.FFT_EXTRACTOR,
            ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
            ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR};

        extractor.setSegmentionParam(segmentionParam);
        extractor.setExtractors(extractors);
    }

    protected MarkerSet getSegementedMarkers(MarkerSetHolder markerSetHolder) {
        MarkerSet segments = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
        if (segments == null) {
            segments = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
        }else{
            
            Collections2.filter(segments.getMarkers(), new Predicate<Marker>(){
                public boolean apply(Marker filterMarker) {
                    return "...".equals(filterMarker.getLabel().trim());
                }
                
            }).clear();
            
        }
        return segments;
    }

    protected void clearCorpus() {
        for (CorpusEntry corpusEntry : corpusRepository.findAllEntries()) {
            corpusRepository.delete(corpusEntry);
        }
        corpusRepository.flush();
    }

    protected void verifyMatches() {
        File testDir = new File(learnDir, "../test");
        for (File filePath : testDir.listFiles(new WavFileNameFilter())) {
            List<CorpusEntry> entries = extractor.extractInMemory(filePath);
            log.debug("accept: {0}:{1}", filePath, entries);
            int index = 0;
            Map<Integer, String> results = new LinkedHashMap<Integer, String>();
            for (CorpusEntry corpusEntry : entries) {
                RecognitionResult result = corpusService.matchByCorpusEntry(corpusEntry);
                index++;
                results.put(index, result.getInfo().getName());
            }
            for (Map.Entry<Integer, String> resultEntry : results.entrySet()) {
                log.debug("[testExtract]result {0}:{1}", resultEntry.getKey(), resultEntry.getValue());
            }
        }
    }

    public CorpusRepositoryFileImpl getCorpusRepository() {
        return corpusRepository;
    }

    public void setCorpusRepository(CorpusRepositoryFileImpl corpusRepository) {
        this.corpusRepository = corpusRepository;
    }

    public CorpusServiceBaseImpl getCorpusService() {
        return corpusService;
    }

    public void setCorpusService(CorpusServiceBaseImpl corpusService) {
        this.corpusService = corpusService;
    }

    public CorpusEntryExtractorFileImpl getExtractor() {
        return extractor;
    }

    public void setExtractor(CorpusEntryExtractorFileImpl extractor) {
        this.extractor = extractor;
    }

    public File getLearnDir() {
        return learnDir;
    }

    public void setLearnDir(File learnDir) {
        this.learnDir = learnDir;
    }

    public class WavFileNameFilter implements FilenameFilter {

        public boolean accept(File file, String fileName) {
            return fileName.endsWith(".wav");
        }
    }
}
