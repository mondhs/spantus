package org.spantus.exp.recognition.multi;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.Map;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.wav.AudioManager;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.exp.ExpConfig;
import org.spantus.exp.recognition.dao.QSegmentExpDao;
import org.spantus.exp.recognition.dao.QSegmentExpHsqlDao;
import org.spantus.exp.recognition.domain.QSegmentExp;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.externals.recognition.services.CorpusEntryExtractor;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorTextGridMapImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow;
import org.spantus.utils.FileUtils;
import org.spantus.utils.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class MultiMapper {

	public final static String RULES_PATH = "/home/as/src/spnt-code/spnt-work-ui/src/main/resources/ClassifierRuleBase.csv";

	private static final Logger log = Logger.getLogger(MultiMapper.class);

	private AudioManager audioManager;
	private CorpusEntryExtractorTextGridMapImpl extractor;
	private CorpusServiceBaseImpl corpusService;
	private CorpusRepositoryFileImpl corpusRepository;
//	private MarkerDao markerDao;
//	private File trainDir;
//	private File wavDir;
	private FilenameFilter fileFilter;
	private QSegmentExpDao qSegmentExpDao;
//	private File testDir;
	private String corpusName;
	private ExpConfig expConfig;
	private Boolean recreate ;

	public void init(ExpConfig expConfig,
			FilenameFilter fileFilter, String corpusName) {
//		this.trainDir = trainDir;
//		this.testDir = testDir;
//		this.wavDir = wavDir;
		recreate = true;
		this.expConfig = expConfig;
		this.fileFilter = fileFilter;
		this.setCorpusName(corpusName);
		
		corpusRepository = new CorpusRepositoryFileImpl();
		corpusRepository.setRepositoryPath(expConfig.getCorpusDirAsFile().getAbsolutePath());
		
		corpusService = new CorpusServiceBaseImpl();
		corpusService.setCorpus(corpusRepository);
		corpusService.setJavaMLSearchWindow(JavaMLSearchWindow.ExpandedResWindow);
		corpusService.setSearchRadius(3);

		
		if (extractor == null) {
			extractor = new CorpusEntryExtractorTextGridMapImpl();
			extractor.setMarkerDir(expConfig.getTrainDirAsFile().getAbsoluteFile()); 
			extractor.setRulesTurnedOn(true);
			extractor.setRulePath(getExpConfig().getRulePath());
			extractor.setWindowLengthInMilSec(expConfig.getWindowLength());
			extractor.setOverlapInPerc(expConfig.getWindowOverlap());
			extractor.setSegmentatorServiceType(expConfig.getSegmentatorServiceType());
			extractor.setClassifier(expConfig.getClassifier());
			log.debug(
					"CorpusEntryExtractorFileImpl created. rulePath: {0}; RulesTurnedOn: {1}",
					extractor.getRulePath(),
					extractor.isRulesTurnedOn());
		}
	
		



		audioManager = AudioManagerFactory.createAudioManager();

		extractor.setCorpusService(corpusService);
		extractor.setWindowLengthInMilSec(getExpConfig().getWindowLength());
		extractor.setOverlapInPerc(getExpConfig().getWindowOverlap());
		extractor.setSegmentionParam(getExpConfig().getSegmentationParam());
		extractor.setExtractors(getExpConfig().getExtractors());
		extractor.getParams().putAll(getExpConfig().getExtractorPramMap());

//		markerDao = WorkServiceFactory.createMarkerDao();


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
		int size = expConfig.getTrainDirAsFile().listFiles(fileFilter).length;
		Double totalTime = 0D;
		for (File texGridFile : expConfig.getTrainDirAsFile().listFiles(fileFilter)) {
			counter++;
			log.error("[extractAndLearn]Processing " + counter + " from " + size
					+ ";  totalTime:" + totalTime + "; file: " + texGridFile);

			File wavFilePath = new File(expConfig.getWavDir(), FileUtils.replaceExtention(
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
			int size = expConfig.getTestDirAsFile().listFiles(fileFilter).length;
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
			for (File texGridFile : expConfig.getTestDirAsFile().listFiles(fileFilter)) {
				counter++;
				log.error("[recognize]Processing "+ counter + " from " + size + "; totalTime:" + totalTime + "; file = " + texGridFile);
				File wavFilePath = new File(expConfig.getWavDir(), FileUtils.replaceExtention(
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
					saveResult(marker,wavFilePath, texGridFile, recogniton, processingTime, this.getCorpusName());
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
	protected void saveResult(Marker marker, File wavFile, File textGridFile, Map<String, RecognitionResult> recogniton, Long processingTime, String currentCorpusName) {
		QSegmentExp exp = createResult(marker, wavFile, textGridFile, recogniton, processingTime, currentCorpusName);
		if(exp == null){
			return;
		}
		saveResult(exp);
	}
	
	protected void saveResult(QSegmentExp exp) {
		getqSegmentExpDao().save(exp);
	}

	protected QSegmentExp createResult(Marker marker, File wavFile, File textGridFile, Map<String, RecognitionResult> recogniton, Long processingTime, String currentCorpusName) {
		String label = getExtractor().createLabelByMarkers(textGridFile, marker);
		label =label.trim();
		label = label.replaceAll("\\d","");
		if(!StringUtils.hasText(label.trim())){
			if(!StringUtils.hasText(recogniton.get("MFCC_EXTRACTOR").getInfo().getName().trim())){
				log.error("NO TEXT. do not save");
				return null;
			}
		}

		
		QSegmentExp exp = new QSegmentExp(wavFile.getName(),
				marker.getStart(),
				marker.getLength(),
				marker.getLabel(),
				currentCorpusName, 
				label, 
				processingTime,
				getLablel(ExtractorEnum.LOUDNESS_EXTRACTOR, recogniton),  getScore(ExtractorEnum.LOUDNESS_EXTRACTOR, recogniton), 
				getLablel(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR, recogniton), getScore(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR, recogniton), 
				getLablel(ExtractorEnum.PLP_EXTRACTOR, recogniton), getScore(ExtractorEnum.PLP_EXTRACTOR, recogniton), 
				getLablel(ExtractorEnum.LPC_EXTRACTOR, recogniton), getScore(ExtractorEnum.LPC_EXTRACTOR, recogniton), 
				getLablel(ExtractorEnum.MFCC_EXTRACTOR, recogniton), getScore(ExtractorEnum.MFCC_EXTRACTOR, recogniton), 
				getLablel(ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR, recogniton), getScore(ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR, recogniton)
				);
		return exp;
	}
		
	
	/**
	 * 
	 * @param recognitionName
	 * @return
	 */
	private String fixRecognitionName(final String recognitionName){
		String fixedRecognitionName = recognitionName;
		//TODO: turn this on due get labels
//		String[] recognitionNames = recognitionName.split("-");
//		if(recognitionNames.length >= 1){
//			fixedRecognitionName = CorpusEntryExtractorTextGridMapImpl.cleanupLabel(recognitionNames[0]);
//		}
		return fixedRecognitionName;
	}
	/**
	 * 
	 * @param extractorEnum
	 * @param recogniton
	 * @return
	 */
	private String getLablel(ExtractorEnum extractorEnum, Map<String, RecognitionResult> recogniton){
		RecognitionResult recognitionResult = recogniton.get(extractorEnum.name());
		String recognitionLabel = "";
		if(recognitionResult != null){
			recognitionLabel = fixRecognitionName(recognitionResult.getInfo().getName());
		}
		return recognitionLabel;
	}
	/**
	 * 
	 * @param extractorEnum
	 * @param recogniton
	 * @return
	 */
	private Double getScore(ExtractorEnum extractorEnum, Map<String, RecognitionResult> recogniton){
		RecognitionResult recognitionResult = recogniton.get(extractorEnum.name());
		return recognitionResult==null?-Double.MAX_VALUE:recognitionResult.getDistance();
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
			((QSegmentExpHsqlDao)qSegmentExpDao).setRecreate(getRecreate());
			qSegmentExpDao.init();
		}
		return qSegmentExpDao;
	}

	private Boolean getRecreate() {
		return recreate;
	}

	public void setqSegmentExpDao(QSegmentExpDao qSegmentExpDao) {
		this.qSegmentExpDao = qSegmentExpDao;
	}

	public String getCorpusName() {
		return corpusName;
	}

	public void setCorpusName(String corpusName) {
		this.corpusName = corpusName;
	}



	public ExpConfig getExpConfig() {
		return expConfig;
	}

	public void setExpConfig(ExpConfig expConfig) {
		this.expConfig = expConfig;
	}

	public void setRecreate(Boolean recreate) {
		this.recreate = recreate;
	}


}
