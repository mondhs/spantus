package org.spantus.work.services;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 */
public abstract class WorkServiceFactory {
	private static MarkerDao markerDao;
	private static ReaderDao readerDao;
	private static  BundleDao bundleDao;
        private static ExtractorReaderService extractorReaderService;

	public static MarkerDao createMarkerDao(){
		if(markerDao == null){
			markerDao = new MarkerProxyDao();
		}
		return markerDao; 
	}
	public static ReaderDao createReaderDao(){
		if(readerDao == null){
			readerDao = new ReaderXmlDaoImpl();
		}
		return readerDao; 
	}
	public static BundleDao createBundleDao(){
		if(bundleDao == null){
			BundleZipDaoImpl _bundleDao = new BundleZipDaoImpl();
			_bundleDao.setMarkerDao(createMarkerDao());
			_bundleDao.setReaderDao(createReaderDao());
			bundleDao = _bundleDao;
		}
		return bundleDao; 
	}
        public static ExtractorReaderService createExtractorReaderService() {
            if(extractorReaderService == null){
                extractorReaderService = new ExtractorReaderServiceImpl();
            }
            return extractorReaderService;
        }

	
}
