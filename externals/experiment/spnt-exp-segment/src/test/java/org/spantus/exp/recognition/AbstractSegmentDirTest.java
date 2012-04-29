/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import org.junit.Before;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.ExpConfig;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.externals.recognition.services.CorpusEntryExtractor;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorFileImpl;
import org.spantus.logger.Logger;
import org.spantus.segment.SegmentationServiceImpl;
import org.spantus.work.services.WorkServiceFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 *
 * @author mondhs
 */
public abstract class AbstractSegmentDirTest {

	private ExpConfig expConfig;
	


    private static final Logger log = Logger.getLogger(AbstractSegmentDirTest.class);

//    public String dirLearn = DIR_LEARN_OUT;

//    private File learnDir;
    private CorpusEntryExtractorFileImpl extractor;
    private CorpusServiceBaseImpl corpusService;
    private CorpusRepositoryFileImpl corpusRepository;
	private MarkerDao markerDao;
	private SegmentationServiceImpl segmentationService;
//	private String rulePath = RULES_PATH;


	public ExpConfig createExpConfig() {
		ExpConfig config = ExpConfig.createConfig();
				return config;
	}

    @Before
    public void onSetup() {
//        learnDir = new File(getDirLearn());
    	expConfig = createExpConfig();
        if(extractor == null){
        	CorpusEntryExtractorFileImpl extractorImpl = new CorpusEntryExtractorFileImpl();
        	extractorImpl.setRulesTurnedOn(true);
        	extractorImpl.setRulePath(getExpConfig().getRootPath()+getExpConfig().getRulePath());
        	log.debug("CorpusEntryExtractorFileImpl created. rulePath: {0}; RulesTurnedOn: {1}", extractorImpl.getRulePath(), extractorImpl.isRulesTurnedOn());
        	this.extractor = extractorImpl;
        }
        corpusService = new CorpusServiceBaseImpl();
        corpusRepository = new CorpusRepositoryFileImpl();
        corpusRepository.setRepositoryPath(getExpConfig().getCorpusDirAsFile().getAbsolutePath());
        corpusService.setCorpus(corpusRepository);
        extractor.setCorpusService(corpusService);
        extractor.setWindowLengthInMilSec(expConfig.getWindowLength());
        extractor.setOverlapInPerc(expConfig.getWindowOverlap());
        extractor.setSegmentionParam(getExpConfig().getSegmentationParam());
        extractor.setExtractors(getExpConfig().getExtractors());
        
        markerDao = WorkServiceFactory.createMarkerDao();
    }



	protected MarkerSet findSegementedMarkers(MarkerSetHolder markerSetHolder) {
		MarkerSet segments = getSegmentationService().findSegementedLowestMarkers(markerSetHolder);

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
        for (SignalSegment corpusEntry : corpusRepository.findAllEntries()) {
            corpusRepository.delete(corpusEntry.getId());
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

//    public File getLearnDir() {
//        return learnDir;
//    }
//
//    public void setLearnDir(File learnDir) {
//        this.learnDir = learnDir;
//    }

    public MarkerDao getMarkerDao() {
		return markerDao;
	}

	public void setMarkerDao(MarkerDao markerDao) {
		this.markerDao = markerDao;
	}

//	public String getDirLearn() {
//		return dirLearn;
//	}
//
//	public void setDirLearn(String dirLearn) {
//		this.dirLearn = dirLearn;
//	}

//	public String getRulePath() {
//		return rulePath;
//	}
//
//	public void setRulePath(String rulePath) {
//		this.rulePath = rulePath;
//	}

	public ExpConfig getExpConfig() {
		return expConfig;
	}

	public void setExpConfig(ExpConfig expConfig) {
		this.expConfig = expConfig;
	}

	public SegmentationServiceImpl getSegmentationService() {
		if(segmentationService == null){
			segmentationService = new SegmentationServiceImpl();
		}
		return segmentationService;
	}

	public void setSegmentationService(SegmentationServiceImpl segmentationService) {
		this.segmentationService = segmentationService;
	}
}
