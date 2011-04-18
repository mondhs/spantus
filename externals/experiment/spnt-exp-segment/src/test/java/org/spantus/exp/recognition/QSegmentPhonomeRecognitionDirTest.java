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
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
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
	}
	
	@After
	public void destroy() {
		qSegmentExpDao.destroy();
	}
	

	@Test
	public void testClassify() {
		int counter = 0;
		for (File texGridFile : getMarkerDir().listFiles(new TextGridNameFilter())) {

			File wavFilePath = new File(getWavDir(), FileUtils.replaceExtention(
					texGridFile, ".wav"));
			log.debug("[testClassify]reading: {0}", wavFilePath);
			MarkerSetHolder markerSetHolder = getExtractor().extract(wavFilePath);
			log.debug("[testClassify]extracted: {0}", markerSetHolder);
			IExtractorInputReader reader = getExtractor().createReaderWithClassifier(wavFilePath);
			MarkerSet ms = findSegementedMarkers(markerSetHolder);

			for (Marker marker : ms) {
				Long start = System.currentTimeMillis();
				RecognitionResult recogniton= getExtractor().match(marker, reader);
				
				if(recogniton == null){
					log.debug("[testClassify]No matches: {0} ", wavFilePath);
					continue;
				}
				log.debug("[testClassify]matching: {0} => {1} === {2}", wavFilePath, 
						marker.getLabel(), 
						recogniton.getInfo().getName()
						);
				
				Long processingTime = System.currentTimeMillis()-start;
				//save result
				saveResult(marker, wavFilePath, recogniton, processingTime);
			}
			
			log.debug("[testClassify]read markers: {0}=>{1}", wavFilePath, ms.getMarkers().size());
			counter++;
		}
		log.debug("[testClassify]read files: {0}", counter);
	}
	/**
	 * 
	 * @param marker
	 * @param wavFilePath
	 * @param recogniton
	 * @param processingTime
	 */
	private void saveResult(Marker marker, File wavFilePath, RecognitionResult recogniton, Long processingTime) {
		Map<String, Float> score = recogniton.getScores();
		String label = getExtractor().createLabel(wavFilePath, marker);
		String recognitionName = recogniton.getInfo().getName();
		String[] recognitionNames = recognitionName.split("-");
		recognitionName = recognitionNames[0].replace("'", "2");
		recognitionName = recognitionName.replace("^", "3");
		recognitionName = recognitionName.replace(":", "1");
		qSegmentExpDao.save(new QSegmentExp(wavFilePath.getName(),
				marker.getLength(),
				marker.getLabel(),
				recognitionName, 
				label, 
				processingTime,
				score.get(ExtractorEnum.LOUDNESS_EXTRACTOR.name()),
				score.get(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name()), 
				score.get(ExtractorEnum.PLP_EXTRACTOR.name()),
				score.get(ExtractorEnum.LPC_EXTRACTOR.name()),
				score.get(ExtractorEnum.MFCC_EXTRACTOR.name()),
				score.get(ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR.name())));		
	}

	public QSegmentExpDao getqSegmentExpDao() {
		return qSegmentExpDao;
	}

	public void setqSegmentExpDao(QSegmentExpDao qSegmentExpDao) {
		this.qSegmentExpDao = qSegmentExpDao;
	}

}
