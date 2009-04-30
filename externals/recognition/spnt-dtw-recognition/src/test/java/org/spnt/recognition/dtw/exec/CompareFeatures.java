package org.spnt.recognition.dtw.exec;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.logger.Logger;
import org.spantus.math.dtw.DtwService;
import org.spantus.math.services.MathServicesFactory;
import org.spantus.work.services.ReaderDao;
import org.spantus.work.services.WorkServiceFactory;

public class CompareFeatures {

	private DtwService dtwService;
	private ReaderDao readerDao;
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(getClass());
	
	
	public Float compareFiles(File sampleFile, File targetFile){
		IExtractorInputReader sampleReader = getReaderDao().read(sampleFile);	
		IExtractorInputReader targetReader = getReaderDao().read(targetFile);
		IGeneralExtractor sampleExtractor = getExtractor("LPC", sampleReader);
		IGeneralExtractor targetExtractor = getExtractor("LPC", targetReader);;
		
		Float distance = calculateDistance(targetExtractor, sampleExtractor);
		return distance;
	}
	/**
	 * 
	 * @param targetExtractor
	 * @param sampleExtractor
	 * @return
	 */
	public Float calculateDistance(IGeneralExtractor targetExtractor, IGeneralExtractor sampleExtractor){
		if(targetExtractor instanceof IExtractor){
			return getDtwService().calculateDistance(
					((IExtractor)targetExtractor).getOutputValues(),
					((IExtractor)sampleExtractor).getOutputValues());
		}
		if(targetExtractor instanceof IExtractorVector){
			return getDtwService().calculateDistanceVector(
					((IExtractorVector)targetExtractor).getOutputValues(),
					((IExtractorVector)sampleExtractor).getOutputValues());
		}
		return null;
		
	}
	
	/**
	 * 
	 * @param name
	 * @param reader
	 * @return
	 */
	public IGeneralExtractor getExtractor(String name, IExtractorInputReader reader){
		for (IExtractor extractor : reader.getExtractorRegister()) {
			if(extractor.getName().contains(name)){
				return extractor;
			}
		}
		for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
			if(extractor.getName().contains(name)){
				return extractor;
			}
		}
		return null;
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
