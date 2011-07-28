/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.spantus.exp.recognition.multi.MultiMapper;
import org.spantus.logger.Logger;

/**
 * 
 * @author mondhs
 */
public class QSegmentPhonomeRecognitionDirTest  {

	private static final Logger log = Logger
			.getLogger(QSegmentPhonomeRecognitionDirTest.class);
	
	private MultiMapper mapper;
	
	@Before
	public void onSetup() {
		mapper = new MultiMapper();
	}
	
	protected void init(MultiMapper mapper, String corpusName) {
		mapper.init(
				new File(QSegmentPhonomeMappingDirTest.ROOT_DIR, "TEST/"+ corpusName +"/"), 
				new File(QSegmentPhonomeMappingDirTest.ROOT_DIR, "TRAIN/"+ corpusName +"/"),
				new File(QSegmentPhonomeMappingDirTest.ROOT_DIR, "CORPUS/"+ corpusName +"/"), 
				new File(QSegmentPhonomeMappingDirTest.ROOT_DIR, "WAV/"+ corpusName +"/"), 
				new TextGridNameFilter(),
				corpusName);
	}
	
	@After
	public void destroy() {
		mapper.destroy();
	}
	
	@Test 
	public void testRecognizeAK1() throws Exception {
			//given
			init(mapper,  "AK1");
			mapper.recognize();
	}
	@Test 
	public void testRecognizeBJ1() throws Exception {
			//given
			init(mapper,  "BJ1");
			mapper.recognize();
	}
	@Test 
	public void testRecognizeLK1() throws Exception {
			//given
			init(mapper,  "LK1");
			mapper.recognize();
	}
	@Test 
	public void testRecognizeTK1() throws Exception {
			//given
			init(mapper,  "TK1");
			mapper.recognize();
	}

//	@Test
//	public void testClassify() throws Exception {
//		int counter = 0;
//		FilenameFilter filter = new TextGridNameFilter();
//		int size = getMarkerDir().listFiles(filter).length;
////		File[] mainList= getMarkerDir().listFiles(filter);
////		List<File> patched = new ArrayList<File>();
////		for (File file : mainList) {
////			patched.add(file);
////			String fileName = file.getAbsolutePath();
////			fileName = fileName.replaceAll("__.mspnt.xml", "");
////			
////			for (String type : new String[]{"airport","babble","car","exhibition","restaurant","station","street","train"}) {
////				for (String level : new String[]{"sn0","sn5","sn10","sn15"}) {
////					Files.copy(file, new File(fileName+"_"+type+"_"+level+".mspnt.xml"));
////				}
////			}
////		}
//		Double totalTime  = 0D;
//		for (File texGridFile : getMarkerDir().listFiles(filter)) {
//			counter++;
//			log.error("[testClassify]Processing "+ counter + " from " + size + "; totalTime:" + totalTime);
//			File wavFilePath = new File(getWavDir(), FileUtils.replaceExtention(
//					texGridFile, ".wav"));
//			log.debug("[testClassify]reading: {0}", wavFilePath);
//			MarkerSetHolder markerSetHolder = getExtractor().extract(wavFilePath);
//			log.debug("[testClassify]extracted: {0}", markerSetHolder);
//			IExtractorInputReader reader = getExtractor().createReaderWithClassifier(wavFilePath);
//			MarkerSet ms = findSegementedMarkers(markerSetHolder);
//
//			for (Marker marker : ms) {
//				Long start = System.currentTimeMillis();
//				Map<String, RecognitionResult> recogniton= getExtractor().bestMatchesForFeatures(wavFilePath.toURI().toURL(), marker, reader);
//				
//				if(recogniton == null){
//					log.debug("[testClassify]No matches: {0} ", wavFilePath);
//					continue;
//				}
////				log.debug("[testClassify]matching: {0} => {1} === {2}", wavFilePath, 
////						marker.getLabel(), 
////						recogniton
////						);
//				
//				Long processingTime = System.currentTimeMillis()-start;
//				
//				//save result
//				saveResult(marker, wavFilePath, recogniton, processingTime);
//			}
//			totalTime += getAudioManager().findLength(wavFilePath.toURI().toURL());
//			log.debug("[testClassify]read markers: {0}=>{1}  [totalTime: {2}]", wavFilePath, ms.getMarkers().size(), totalTime);
//		}
//		log.debug("[testClassify]read files: {0}", counter);
//	}
//
//	/**
//	 * 
//	 * @param marker
//	 * @param wavFilePath
//	 * @param recogniton
//	 * @param processingTime
//	 */
//	private void saveResult(Marker marker, File wavFilePath, Map<String, RecognitionResult> recogniton, Long processingTime) {
//		String label = getExtractor().createLabel(wavFilePath, marker);
//		if(!StringUtils.hasText(label)){
//			log.error("NO TEXT. do not save");
//		}
//		
//		qSegmentExpDao.save(new QSegmentExp(wavFilePath.getName(),
//				marker.getStart(),
//				marker.getLength(),
//				marker.getLabel(),
//				CORPUSNAME, 
//				label, 
//				processingTime,
//				getLablel(ExtractorEnum.LOUDNESS_EXTRACTOR, recogniton),  getScore(ExtractorEnum.LOUDNESS_EXTRACTOR, recogniton), 
//				getLablel(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR, recogniton), getScore(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR, recogniton), 
//				getLablel(ExtractorEnum.PLP_EXTRACTOR, recogniton), getScore(ExtractorEnum.PLP_EXTRACTOR, recogniton), 
//				getLablel(ExtractorEnum.LPC_EXTRACTOR, recogniton), getScore(ExtractorEnum.LPC_EXTRACTOR, recogniton), 
//				getLablel(ExtractorEnum.MFCC_EXTRACTOR, recogniton), getScore(ExtractorEnum.MFCC_EXTRACTOR, recogniton), 
//				getLablel(ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR, recogniton), getScore(ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR, recogniton)
//				));		
//	}
//
//	public QSegmentExpDao getqSegmentExpDao() {
//		return qSegmentExpDao;
//	}
//
//	public void setqSegmentExpDao(QSegmentExpDao qSegmentExpDao) {
//		this.qSegmentExpDao = qSegmentExpDao;
//	}

}
