package org.spnt.recognition.dtw.exec;

import java.io.File;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.logger.Logger;
import org.spantus.math.dtw.DtwService;
import org.spantus.math.services.MathServicesFactory;
import org.spantus.work.services.ReaderDao;
import org.spantus.work.services.WorkServiceFactory;

public class CompareFeatures {

	private DtwService dtwService;
	private ReaderDao readerDao;
	private Logger log = Logger.getLogger(getClass());
	
	
	public void compareFiles(File sampleFile, File targetFile){
		IExtractorInputReader sampleReader = getReaderDao().read(sampleFile);	
		IExtractorInputReader targetReader = getReaderDao().read(targetFile);
		IExtractor sampleExtractor = sampleReader.getExtractorRegister().iterator().next();
		IExtractor targetExtractor = targetReader.getExtractorRegister().iterator().next();
		Float distance = getDtwService().calculateDistance(targetExtractor.getOutputValues(),
				sampleExtractor.getOutputValues());
		log.error("Distance: " + distance);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String samplePath="./test/du.sspnt.xml";
		String targetPath="./test/vienas.sspnt.xml";
		File sampleFile = new File(samplePath);
		File targetFile = new File(targetPath);
		CompareFeatures compareFeatures = new CompareFeatures();
		compareFeatures.compareFiles(sampleFile, targetFile);
		
	}

	
	public DtwService getDtwService() {
		if(dtwService == null){
			dtwService = MathServicesFactory.createDtwService();
		}
		return dtwService;
	}
	
	public ReaderDao getReaderDao() {
		if (readerDao == null) {
			readerDao = WorkServiceFactory.createReaderDao();
		}
		return readerDao;
	}
	

}
