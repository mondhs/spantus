package org.spantus.exp.recognition.multi;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.wav.AudioManager;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.exp.recognition.dao.QSegmentExpDao;
import org.spantus.exp.recognition.dao.QSegmentExpHsqlDao;
import org.spantus.exp.recognition.domain.QSegmentExp;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.externals.recognition.services.CorpusEntryExtractor;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorFileImpl;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorTextGridMapImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.logger.Logger;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.utils.FileUtils;
import org.spantus.utils.StringUtils;
import org.spantus.work.services.MarkerDao;
import org.spantus.work.services.WorkServiceFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class MultiMapper {

	public static final int WINDOW_OVERLAP = 66;
	public static final int WINDOW_LENGTH = 33;
	public final static String RULES_PATH = "/home/mgreibus/src/spantus-svn/trunk/spnt-work-ui/src/main/resources/ClassifierRuleBase.csv";

	private static final Logger log = Logger.getLogger(MultiMapper.class);

	private AudioManager audioManager;
	private CorpusEntryExtractorTextGridMapImpl extractor;
	private CorpusServiceBaseImpl corpusService;
	private CorpusRepositoryFileImpl corpusRepository;
	private MarkerDao markerDao;
	private File trainDir;
	private File wavDir;
	private FilenameFilter fileFilter;
	private QSegmentExpDao qSegmentExpDao;
	private File testDir;
	private String corpusName;

	public void init(File testDir, File trainDir, File corpusDir,  File wavDir,
			FilenameFilter fileFilter, String corpusName) {
		this.trainDir = trainDir;
		this.testDir = testDir;
		this.wavDir = wavDir;
		this.fileFilter = fileFilter;
		this.corpusName = corpusName;
		
		corpusRepository = new CorpusRepositoryFileImpl();
		corpusRepository.setRepositoryPath(corpusDir.getAbsolutePath());
		
		corpusService = new CorpusServiceBaseImpl();
		corpusService.setCorpus(corpusRepository);
//		corpusService.setJavaMLSearchWindow(JavaMLSearchWindow.ExpandedResWindow);
//		corpusService.setSearchRadius(3);

		
		if (extractor == null) {
			extractor = new CorpusEntryExtractorTextGridMapImpl();
			extractor.setMarkerDir(trainDir); 
			extractor.setRulesTurnedOn(true);
			extractor.setRulePath(RULES_PATH);
			log.debug(
					"CorpusEntryExtractorFileImpl created. rulePath: {0}; RulesTurnedOn: {1}",
					extractor.getRulePath(),
					extractor.isRulesTurnedOn());
		}
	
		



		audioManager = AudioManagerFactory.createAudioManager();

		extractor.setCorpusService(corpusService);
		extractor.setWindowLengthInMilSec(WINDOW_LENGTH);
		extractor.setOverlapInPerc(WINDOW_OVERLAP);
		OnlineDecisionSegmentatorParam segmentionParam = new OnlineDecisionSegmentatorParam();
		segmentionParam.setMinLength(91L);
		segmentionParam.setMinSpace(261L);
		segmentionParam.setExpandStart(260L);
		segmentionParam.setExpandEnd(360L);

		ExtractorEnum[] extractors = new ExtractorEnum[] {
				ExtractorEnum.MFCC_EXTRACTOR,
				ExtractorEnum.PLP_EXTRACTOR,
				ExtractorEnum.LPC_EXTRACTOR,
				// ExtractorEnum.FFT_EXTRACTOR,
				ExtractorEnum.LOUDNESS_EXTRACTOR,
				ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
				ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR };

		extractor.setSegmentionParam(segmentionParam);
		extractor.setExtractors(extractors);
		
		
		ExtractorParam smothParam = new ExtractorParam();
//		ExtractorParamUtils.setValue(smothParam, ExtractorModifiersEnum.smooth.name(), Boolean.TRUE);
//		extractor.setParams(new HashMap<String, ExtractorParam>());
//		extractor.getParams().put(ExtractorEnum.LOUDNESS_EXTRACTOR.name(), smothParam);
//		extractor.getParams().put(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name(), smothParam);
//		extractor.getParams().put(ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR.name(), smothParam);
		

		markerDao = WorkServiceFactory.createMarkerDao();


	}
	
	/**
	 * 
	 */
    public  void clearCorpus() {
        for (CorpusEntry corpusEntry : corpusRepository.findAllEntries()) {
            corpusRepository.delete(corpusEntry);
        }
        corpusRepository.flush();
    }
    /**
     * 
     * @throws MalformedURLException
     */
	public void extractAndLearn() throws MalformedURLException {
		int counter = 0;
		// FilenameFilter fileFilter = new TextGridNameFilter();
		int size = trainDir.listFiles(fileFilter).length;
		Double totalTime = 0D;
		for (File texGridFile : trainDir.listFiles(fileFilter)) {
			counter++;
			log.error("[extractAndLearn]Processing " + counter + " from " + size
					+ ";  totalTime:" + totalTime + "; file: " + texGridFile);

			File wavFilePath = new File(wavDir, FileUtils.replaceExtention(
					texGridFile, ".wav"));
			// if(!filePath.getName().contains("far1")){
			// continue;
			// }
			// log.debug("[testClassify]reading: {0}", filePath);
			MarkerSetHolder markerSetHolder = null;
			try{
			markerSetHolder = getExtractor().extractAndLearn(
					wavFilePath.getAbsoluteFile());
			}catch (NullPointerException e) {
				log.error(e);
				continue;
			}
			totalTime += getAudioManager().findLength(
					wavFilePath.toURI().toURL());
			log.debug("accept: {0}:{1}", texGridFile, markerSetHolder);
		}
	}
	
	public void recognize() throws MalformedURLException{
			int counter = 0;
			int size = testDir.listFiles(fileFilter).length;
//			File[] mainList= getMarkerDir().listFiles(filter);
//			List<File> patched = new ArrayList<File>();
//			for (File file : mainList) {
//				patched.add(file);
//				String fileName = file.getAbsolutePath();
//				fileName = fileName.replaceAll("__.mspnt.xml", "");
//				
//				for (String type : new String[]{"airport","babble","car","exhibition","restaurant","station","street","train"}) {
//					for (String level : new String[]{"sn0","sn5","sn10","sn15"}) {
//						Files.copy(file, new File(fileName+"_"+type+"_"+level+".mspnt.xml"));
//					}
//				}
//			}
			Double totalTime  = 0D;
			for (File texGridFile : testDir.listFiles(fileFilter)) {
				counter++;
				log.error("[recognize]Processing "+ counter + " from " + size + "; totalTime:" + totalTime + "; file = " + texGridFile);
				File wavFilePath = new File(wavDir, FileUtils.replaceExtention(
						texGridFile, ".wav"));
				log.debug("[recognize]reading: {0}", wavFilePath);
				MarkerSetHolder markerSetHolder = getExtractor().extract(wavFilePath);
				log.debug("[recognize]extracted: {0}", markerSetHolder);
				IExtractorInputReader reader = getExtractor().createReaderWithClassifier(wavFilePath);
				MarkerSet ms = findSegementedMarkers(markerSetHolder);

				for (Marker marker : ms) {
					Long start = System.currentTimeMillis();
					Map<String, RecognitionResult> recogniton= getExtractor().bestMatchesForFeatures(wavFilePath.toURI().toURL(), marker, reader);
					
					if(recogniton == null){
						log.debug("[recognize]No matches: {0} ", wavFilePath);
						continue;
					}
//					log.debug("[testClassify]matching: {0} => {1} === {2}", wavFilePath, 
//							marker.getLabel(), 
//							recogniton
//							);
					
					Long processingTime = System.currentTimeMillis()-start;
					
					//save result
					saveResult(marker, texGridFile, recogniton, processingTime);
				}
				totalTime += getAudioManager().findLength(wavFilePath.toURI().toURL());
				log.debug("[recognize]read markers: {0}=>{1}  [totalTime: {2}]", wavFilePath, ms.getMarkers().size(), totalTime);
			}
			log.debug("[recognize]read files: {0}", counter);
		}
	/**
	 * 
	 * @param marker
	 * @param wavFilePath
	 * @param recogniton
	 * @param processingTime
	 */
	private void saveResult(Marker marker, File textGridFile, Map<String, RecognitionResult> recogniton, Long processingTime) {
		String label = getExtractor().createLabelByMarkers(textGridFile, marker);
		if(!StringUtils.hasText(label)){
			log.error("NO TEXT. do not save");
		}
		
		getqSegmentExpDao().save(new QSegmentExp(textGridFile.getName(),
				marker.getStart(),
				marker.getLength(),
				marker.getLabel(),
				"TRI4_"+corpusName, 
				label, 
				processingTime,
				getLablel(ExtractorEnum.LOUDNESS_EXTRACTOR, recogniton),  getScore(ExtractorEnum.LOUDNESS_EXTRACTOR, recogniton), 
				getLablel(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR, recogniton), getScore(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR, recogniton), 
				getLablel(ExtractorEnum.PLP_EXTRACTOR, recogniton), getScore(ExtractorEnum.PLP_EXTRACTOR, recogniton), 
				getLablel(ExtractorEnum.LPC_EXTRACTOR, recogniton), getScore(ExtractorEnum.LPC_EXTRACTOR, recogniton), 
				getLablel(ExtractorEnum.MFCC_EXTRACTOR, recogniton), getScore(ExtractorEnum.MFCC_EXTRACTOR, recogniton), 
				getLablel(ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR, recogniton), getScore(ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR, recogniton)
				));		
	}
	
	
	/**
	 * 
	 * @param recognitionName
	 * @return
	 */
	private String fixRecognitionName(final String recognitionName){
		String fixedRecognitionName = recognitionName;
		String[] recognitionNames = recognitionName.split("-");
		if(recognitionNames.length >= 1){
			fixedRecognitionName = CorpusEntryExtractorTextGridMapImpl.cleanupLabel(recognitionNames[0]);
		}
		return fixedRecognitionName;
	}
	/**
	 * 
	 * @param extractorEnum
	 * @param recogniton
	 * @return
	 */
	private String getLablel(ExtractorEnum extractorEnum, Map<String, RecognitionResult> recogniton){
		return fixRecognitionName(recogniton.get(extractorEnum.name()).getInfo().getName());
	}
	/**
	 * 
	 * @param extractorEnum
	 * @param recogniton
	 * @return
	 */
	private Double getScore(ExtractorEnum extractorEnum, Map<String, RecognitionResult> recogniton){
		return recogniton.get(extractorEnum.name()).getDistance();
	}
	/**
	 * 
	 * @param markerSetHolder
	 * @return
	 */
	protected MarkerSet findSegementedMarkers(MarkerSetHolder markerSetHolder) {
		MarkerSet segments = getExtractor().findSegementedLowestMarkers(markerSetHolder);

		Collections2.filter(segments.getMarkers(), new Predicate<Marker>() {
			public boolean apply(Marker filterMarker) {
				return "...".equals(filterMarker.getLabel().trim()) ||
				"-".equals(filterMarker.getLabel().trim())
				;
			}

		}).clear();

		return segments;
	}
	/**
	 * 
	 */
	public void destroy() {
		getqSegmentExpDao().destroy();
	}

	public CorpusEntryExtractor getExtractor() {
		return extractor;
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}

	public void setAudioManager(AudioManager audioManager) {
		this.audioManager = audioManager;
	}
	public QSegmentExpDao getqSegmentExpDao() {
		if(qSegmentExpDao == null){
			qSegmentExpDao = new QSegmentExpHsqlDao();
			qSegmentExpDao.init();
		}
		return qSegmentExpDao;
	}

	public void setqSegmentExpDao(QSegmentExpDao qSegmentExpDao) {
		this.qSegmentExpDao = qSegmentExpDao;
	}


}
