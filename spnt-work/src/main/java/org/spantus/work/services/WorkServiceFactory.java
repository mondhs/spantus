package org.spantus.work.services;

import org.spantus.extractor.segments.online.ExtremeOnClassifierServiceFactory;
import org.spantus.extractor.segments.online.ExtremeOnlineRuleClassifier;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.utils.StringUtils;
import org.spantus.work.extractor.segments.online.rule.ClassifierRuleBaseServiceFileMvelImpl;
import org.spantus.work.extractor.segments.online.rule.ClassifierRuleBaseServiceMvelImpl;
import org.spantus.work.services.impl.BundleZipDaoImpl;
import org.spantus.work.services.impl.ExtractorReaderServiceImpl;
import org.spantus.work.services.impl.MarkerProxyDao;
import org.spantus.work.services.impl.ReaderXmlDaoImpl;
import org.spantus.work.services.reader.ExternalReaderDao;
import org.spantus.work.services.reader.impl.CsvDaoImpl;
import org.spantus.work.services.reader.impl.WekaArffDaoImpl;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 */
public abstract class WorkServiceFactory {
	private static MarkerDao markerDao;
	private static ReaderDao readerDao;
	private static BundleDao bundleDao;
	private static ExtractorReaderService extractorReaderService;
	private static ExternalReaderDao wekaArffDao;
	private static ExternalReaderDao csvDao;

	public static MarkerDao createMarkerDao() {
		if (markerDao == null) {
			markerDao = new MarkerProxyDao();
		}
		return markerDao;
	}

	public static ReaderDao createReaderDao() {
		if (readerDao == null) {
			readerDao = new ReaderXmlDaoImpl();
		}
		return readerDao;
	}

	public static ExternalReaderDao createWekaArffDao() {
		if (wekaArffDao == null) {
			wekaArffDao = new WekaArffDaoImpl();
		}
		return wekaArffDao;
	}

	public static ExternalReaderDao createCsvDao() {
		if (csvDao == null) {
			csvDao = new CsvDaoImpl();
		}
		return csvDao;
	}
	
	
	public static BundleDao createBundleDao() {
		if (bundleDao == null) {
			BundleZipDaoImpl _bundleDao = new BundleZipDaoImpl();
			_bundleDao.setMarkerDao(createMarkerDao());
			_bundleDao.setReaderDao(createReaderDao());
			bundleDao = _bundleDao;
		}
		return bundleDao;
	}

	public static ExtractorReaderService createExtractorReaderService() {
		if (extractorReaderService == null) {
			extractorReaderService = new ExtractorReaderServiceImpl();
		}
		return extractorReaderService;
	}
	public static ClassifierRuleBaseService createClassifierRuleBaseService() {
		ClassifierRuleBaseServiceMvelImpl ruleBase = new ClassifierRuleBaseServiceFileMvelImpl();
		ruleBase.setClusterService(ExtremeOnClassifierServiceFactory.createClusterService());
		
//		ClassifierPostProcessServiceBaseImpl ruleBase = new ClassifierPostProcessServiceBaseImpl();
		return ruleBase;
	}
	/**
	 * 
	 * @param rulePath
	 * @return
	 */
	public static ClassifierRuleBaseService createClassifierRuleBaseService(String rulePath) {
		ClassifierRuleBaseServiceFileMvelImpl ruleBase = new ClassifierRuleBaseServiceFileMvelImpl();
		if(rulePath != null){
			ruleBase.setPath(rulePath);
		}
		ruleBase.setClusterService(ExtremeOnClassifierServiceFactory.createClusterService());
		
//		ClassifierPostProcessServiceBaseImpl ruleBase = new ClassifierPostProcessServiceBaseImpl();
		return ruleBase;
	}
	
	public static void udpateClassifierRuleBaseService(
			ExtremeOnlineRuleClassifier classifier, String path) {
		if(StringUtils.hasText(path)){
			classifier.setRuleBaseService(WorkServiceFactory.createClassifierRuleBaseService(path));
		}else{
			classifier.setRuleBaseService(WorkServiceFactory.createClassifierRuleBaseService(path));
		}
	}


}
