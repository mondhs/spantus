/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.recognition.dao.QSegmentExpDao;
import org.spantus.exp.recognition.dao.QSegmentExpHsqlDao;
import org.spantus.exp.recognition.domain.QSegmentExp;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorFileImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow;
import org.spantus.utils.FileUtils;

/**
 * 
 * @author mondhs
 */
public class QSegmentPhonomeRecognitionDirTest extends
		QSegmentPhonomeMappingDirTest {

	private static final Logger log = Logger
			.getLogger(QSegmentPhonomeRecognitionDirTest.class);
	
	private QSegmentExpDao qSegmentExpDao;
	private File markerDir = new File(DIR_LEARN_WAV, "GRID/TEST/");
	
	@Before
	public void onSetup() {
		setMarkerDir(markerDir);
		super.onSetup();
		QSegmentExpHsqlDao qSegmentExpDaoImpl = new QSegmentExpHsqlDao();
		qSegmentExpDaoImpl.setRecreate(true);
		qSegmentExpDaoImpl.init();
		setqSegmentExpDao(qSegmentExpDaoImpl);
		
		CorpusServiceBaseImpl corpusServiceBaseImpl = 
		((CorpusServiceBaseImpl)((CorpusEntryExtractorFileImpl)getExtractor()).getCorpusService());
		corpusServiceBaseImpl.setJavaMLSearchWindow(JavaMLSearchWindow.LinearWindow);
		corpusServiceBaseImpl.setSearchRadius(5);
		
	}
	
	@After
	public void destroy() {
		qSegmentExpDao.destroy();
	}
	

	@Test
	public void testClassify() throws Exception {
		int counter = 0;
		int size = getMarkerDir().listFiles(new TextGridNameFilter()).length;
		for (File texGridFile : getMarkerDir().listFiles(new TextGridNameFilter())) {
			counter++;
			log.error("[testClassify]Processing "+ counter + " from " + size);
			File wavFilePath = new File(getWavDir(), FileUtils.replaceExtention(
					texGridFile, ".wav"));
			log.debug("[testClassify]reading: {0}", wavFilePath);
			MarkerSetHolder markerSetHolder = getExtractor().extract(wavFilePath);
			log.debug("[testClassify]extracted: {0}", markerSetHolder);
			IExtractorInputReader reader = getExtractor().createReaderWithClassifier(wavFilePath);
			MarkerSet ms = findSegementedMarkers(markerSetHolder);

			for (Marker marker : ms) {
				Long start = System.currentTimeMillis();
				Map<String, RecognitionResult> recogniton= getExtractor().bestMatchesForFeatures(wavFilePath.toURI().toURL(), marker, reader);
				
				if(recogniton == null){
					log.debug("[testClassify]No matches: {0} ", wavFilePath);
					continue;
				}
				log.debug("[testClassify]matching: {0} => {1} === {2}", wavFilePath, 
						marker.getLabel(), 
						recogniton
						);
				
				Long processingTime = System.currentTimeMillis()-start;
				//save result
				saveResult(marker, wavFilePath, recogniton, processingTime);
			}
			
			log.debug("[testClassify]read markers: {0}=>{1}", wavFilePath, ms.getMarkers().size());
		}
		log.debug("[testClassify]read files: {0}", counter);
	}
	/**
	 * 
	 * @param recognitionName
	 * @return
	 */
	public String fixRecognitionName(final String recognitionName){
		String fixedRecognitionName = recognitionName;
		String[] recognitionNames = recognitionName.split("-");
		if(recognitionNames.length >= 1){
			fixedRecognitionName = recognitionNames[0].replace("'", "2");
			fixedRecognitionName = recognitionName.replace("^", "3");
			fixedRecognitionName = recognitionName.replace(":", "1");
		}
		return fixedRecognitionName;
	}
	
	private String getLablel(ExtractorEnum extractorEnum, Map<String, RecognitionResult> recogniton){
		return fixRecognitionName(recogniton.get(extractorEnum.name()).getInfo().getName());
	}
	private Float getScore(ExtractorEnum extractorEnum, Map<String, RecognitionResult> recogniton){
		return recogniton.get(extractorEnum.name()).getDistance();
	}
	/**
	 * 
	 * @param marker
	 * @param wavFilePath
	 * @param recogniton
	 * @param processingTime
	 */
	private void saveResult(Marker marker, File wavFilePath, Map<String, RecognitionResult> recogniton, Long processingTime) {
		String label = getExtractor().createLabel(wavFilePath, marker);
		
		
		qSegmentExpDao.save(new QSegmentExp(wavFilePath.getName(),
				marker.getStart(),
				marker.getLength(),
				marker.getLabel(),
				"", 
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

	public QSegmentExpDao getqSegmentExpDao() {
		return qSegmentExpDao;
	}

	public void setqSegmentExpDao(QSegmentExpDao qSegmentExpDao) {
		this.qSegmentExpDao = qSegmentExpDao;
	}

}
