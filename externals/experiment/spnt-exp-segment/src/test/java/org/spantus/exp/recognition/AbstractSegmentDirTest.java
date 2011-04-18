/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.Before;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.externals.recognition.services.CorpusEntryExtractor;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorFileImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.work.services.MarkerDao;
import org.spantus.work.services.WorkServiceFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 *
 * @author mondhs
 */
public abstract class AbstractSegmentDirTest {

    public static final int WINDOW_OVERLAP = 66;
	public static final int WINDOW_LENGTH = 33;
	public final static String DIR_LEARN_WAV =
//        "/mnt/audio/VDU_ISO4"    
    	"/home/mgreibus/src/garsynai/VDU/MG/" 
		//            "/home/mondhs/src/garsynai/skaiciai/learn"
            ;
    public final static String DIR_LEARN_OUT =
            "/home/mgreibus/src/garsynai/VDU/MG/OUTPUT/"
//            "./target/learn-corpus/"
            ;
    public final static String RULES_PATH =
        "/home/mgreibus/src/spantus-svn/trunk/spnt-work-ui/src/main/resources";
    private static final Logger log = Logger.getLogger(AbstractSegmentDirTest.class);

    public String dirLearn = DIR_LEARN_OUT;

    private File learnDir;
    private CorpusEntryExtractorFileImpl extractor;
    private CorpusServiceBaseImpl corpusService;
    private CorpusRepositoryFileImpl corpusRepository;
	private MarkerDao markerDao;
	private String rulePath = RULES_PATH;


    @Before
    public void onSetup() {
        learnDir = new File(getDirLearn());
        if(extractor == null){
        	CorpusEntryExtractorFileImpl extractorImpl = new CorpusEntryExtractorFileImpl();
        	extractorImpl.setRulesTurnedOn(true);
        	extractorImpl.setRulePath(getRulePath());
        	log.debug("CorpusEntryExtractorFileImpl created. rulePath: {0}; RulesTurnedOn: {1}", extractorImpl.getRulePath(), extractorImpl.isRulesTurnedOn());
        	this.extractor = extractorImpl;
        }
        corpusService = new CorpusServiceBaseImpl();
        corpusRepository = new CorpusRepositoryFileImpl();
        corpusRepository.setRepositoryPath(getDirLearn());
        corpusService.setCorpus(corpusRepository);
        extractor.setCorpusService(corpusService);
        extractor.setWindowLengthInMilSec(WINDOW_LENGTH);
        extractor.setOverlapInPerc(WINDOW_OVERLAP);
        
        OnlineDecisionSegmentatorParam segmentionParam = new OnlineDecisionSegmentatorParam();
        segmentionParam.setMinLength(91L);
        segmentionParam.setMinSpace(261L);
        segmentionParam.setExpandStart(260L);
        segmentionParam.setExpandEnd(360L);
        extractor.setSegmentionParam(segmentionParam);

        
        ExtractorEnum[] extractors = new ExtractorEnum[]{
            ExtractorEnum.MFCC_EXTRACTOR,
            ExtractorEnum.PLP_EXTRACTOR,
            ExtractorEnum.LPC_EXTRACTOR,
//            ExtractorEnum.FFT_EXTRACTOR,
            ExtractorEnum.LOUDNESS_EXTRACTOR,
            ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
            ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR};

        extractor.setExtractors(extractors);
        
        markerDao = WorkServiceFactory.createMarkerDao();
    }

	protected MarkerSet findSegementedMarkers(MarkerSetHolder markerSetHolder) {
		MarkerSet segments = getExtractor().findSegementedHighestMarkers(markerSetHolder);

		Collections2.filter(segments.getMarkers(), new Predicate<Marker>() {
			public boolean apply(Marker filterMarker) {
				return "...".equals(filterMarker.getLabel().trim()) ||
				"-".equals(filterMarker.getLabel().trim())
				;
			}

		}).clear();

		return segments;
	}

    protected void clearCorpus() {
        for (CorpusEntry corpusEntry : corpusRepository.findAllEntries()) {
            corpusRepository.delete(corpusEntry);
        }
        corpusRepository.flush();
    }

//    protected void verifyMatches() {
//        File testDir = new File(learnDir, "../test");
//        for (File filePath : testDir.listFiles(new WavFileNameFilter())) {
//            List<CorpusEntry> entries = extractor.extractInMemory(filePath);
//            log.debug("accept: {0}:{1}", filePath, entries);
//            int index = 0;
//            Map<Integer, String> results = new LinkedHashMap<Integer, String>();
//            for (CorpusEntry corpusEntry : entries) {
//                RecognitionResult result = corpusService.matchByCorpusEntry(corpusEntry);
//                index++;
//                results.put(index, result.getInfo().getName());
//            }
//            for (Map.Entry<Integer, String> resultEntry : results.entrySet()) {
//                log.debug("[testExtract]result {0}:{1}", resultEntry.getKey(), resultEntry.getValue());
//            }
//        }
//    }

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

    public CorpusEntryExtractor getExtractor() {
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
    public class TextGridNameFilter implements FilenameFilter {

        public boolean accept(File file, String fileName) {
            return fileName.endsWith(".TextGrid");
        }
    }
	public MarkerDao getMarkerDao() {
		return markerDao;
	}

	public void setMarkerDao(MarkerDao markerDao) {
		this.markerDao = markerDao;
	}

	public String getDirLearn() {
		return dirLearn;
	}

	public void setDirLearn(String dirLearn) {
		this.dirLearn = dirLearn;
	}

	public String getRulePath() {
		return rulePath;
	}

	public void setRulePath(String rulePath) {
		this.rulePath = rulePath;
	}
}
