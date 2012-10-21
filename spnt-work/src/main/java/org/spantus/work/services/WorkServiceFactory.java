package org.spantus.work.services;

import org.spantus.core.dao.SignalSegmentDao;
import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.extractor.dao.ReaderDao;
import org.spantus.core.service.ExtractorInputReaderService;
import org.spantus.core.service.impl.ExtractorInputReaderServiceImpl;
import org.spantus.extractor.segments.online.ExtremeOnClassifierServiceFactory;
import org.spantus.extractor.segments.online.ExtremeOnlineRuleClassifier;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseServiceBaseImpl;
import org.spantus.utils.StringUtils;
import org.spantus.work.extractor.segments.online.rule.ClassifierRuleBaseServiceFileMvelImpl;
import org.spantus.work.extractor.segments.online.rule.ClassifierRuleBaseServiceMvelImpl;
import org.spantus.work.services.impl.BundleZipDaoImpl;
import org.spantus.work.services.impl.SignalSegmentSimpleJsonDao;
import org.spantus.work.services.impl.WorkExtractorReaderServiceImpl;
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
	private static MarkerProxyDao markerDao;
	private static ReaderDao readerDao;
	private static BundleDao bundleDao;
	private static WorkExtractorReaderService extractorReaderService;
	private static ExternalReaderDao wekaArffDao;
	private static ExternalReaderDao csvDao;
	private static SignalSegmentDao signalSegmentDao;
	private static ExtractorInputReaderService extractorInputReaderService;

	public static MarkerDao createMarkerDao() {
		if (markerDao == null) {
			markerDao = new MarkerProxyDao();
		}
		return markerDao;
	}
        /**
         * Resolve format by {@link MarkerProxyDao#resolveMarkerDao(java.lang.String) }
         * @param format
         * @return 
         */
        public static MarkerDao resolveMarkerDao(String format) {
            createMarkerDao();
            return  markerDao.resolveMarkerDao(format); 
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
	public static SignalSegmentDao createSignalSegmentDao() {
		if (signalSegmentDao == null) {
			signalSegmentDao = new SignalSegmentSimpleJsonDao();
		}
		return signalSegmentDao;
	}

	public static ExtractorInputReaderService createExtractorInputReaderService() {
		if (extractorInputReaderService == null) {
			extractorInputReaderService = new ExtractorInputReaderServiceImpl();
		}
		return extractorInputReaderService;
	}
	
	public static BundleDao createBundleDao() {
		if (bundleDao == null) {
			BundleZipDaoImpl _bundleDao = new BundleZipDaoImpl();
			_bundleDao.setMarkerDao(createMarkerDao());
			_bundleDao.setReaderDao(createReaderDao());
			_bundleDao.setSignalSegmentDao(createSignalSegmentDao());
			_bundleDao.setExtractorInputReaderService(createExtractorInputReaderService());
			bundleDao = _bundleDao;
		}
		return bundleDao;
	}




	public static WorkExtractorReaderService createExtractorReaderService() {
		if (extractorReaderService == null) {
			WorkExtractorReaderServiceImpl extractorReaderServiceImpl = new WorkExtractorReaderServiceImpl();
                        extractorReaderService = extractorReaderServiceImpl;
		}
		return extractorReaderService;
	}
        public static WorkExtractorReaderService createExtractorReaderService(int windowLengthInMilSec, int overlapInPerc) {
            WorkExtractorReaderServiceImpl extractorReaderServiceImpl = new WorkExtractorReaderServiceImpl();
            extractorReaderServiceImpl.setWindowLengthInMilSec(windowLengthInMilSec);
            extractorReaderServiceImpl.setOverlapInPerc(overlapInPerc);
            return extractorReaderServiceImpl;
        }
	public static ClassifierRuleBaseService createClassifierRuleBaseService() {
		return createClassifierRuleBaseService(null);
	}
	/**
	 * 
	 * @param rulePath
	 * @return
	 */
	public static ClassifierRuleBaseService createClassifierRuleBaseService(String rulePath) {
//		ClassifierRuleBaseService ruleBase = new ClassifierRuleBaseServiceBaseImpl(); 
		ClassifierRuleBaseServiceFileMvelImpl ruleBase = new ClassifierRuleBaseServiceFileMvelImpl();
		if(rulePath != null){
			ruleBase.setPath(rulePath);
		}
		ruleBase.setClusterService(ExtremeOnClassifierServiceFactory.createClusterService());
		
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
