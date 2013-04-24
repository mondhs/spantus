package org.spantus.extr.wordspot.service.impl.test;

import java.io.File;
import junit.framework.Assert;
import org.junit.Before;
import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.service.IMarkerService;
import org.spantus.core.marker.service.MarkerServiceFactory;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
import org.spantus.extr.wordspot.service.impl.SegmentExtractorServiceImpl;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.WorkServiceFactory;

public abstract class AbstractSegmentExtractorTest {

    private SegmentExtractorServiceImpl segmentExtractorService;
    private File repositoryPathRoot;
    private File wavFile;
    private File repositoryPath;
    private MarkerDao markerDao;
    private IMarkerService markerService;
    protected SegmentExtractorServiceConfig serviceConfig = new SegmentExtractorServiceConfig();
        

    protected File createRepositoryPathRoot(){
        return  new File("../../../data");
    }
    
    protected File createRepositoryPath(File aRepositoryPathRoot){
        return  new File(aRepositoryPathRoot,"CORPUS/phone");
    }
    
     protected File createWavFile(File aRepositoryPathRoot){
        return  new File(aRepositoryPathRoot,"text1-8000.wav");
    }

    @Before
    public void setUp() throws Exception {
        repositoryPathRoot =createRepositoryPathRoot();
        wavFile = createWavFile(repositoryPathRoot);
        repositoryPath = createRepositoryPath(repositoryPathRoot);

        Assert.assertTrue("repositoryPath exists " + getRepositoryPath().getAbsolutePath(), getRepositoryPath().exists());
        Assert.assertTrue("repositoryPath is directory", getRepositoryPath().isDirectory());
        Assert.assertTrue("wavFile not exists" + getWavFile(), getWavFile().exists());
        segmentExtractorService = new SegmentExtractorServiceImpl();
        segmentExtractorService.setServiceConfig(serviceConfig);
        segmentExtractorService.getServiceConfig().setRepositoryPath(getRepositoryPath().getAbsolutePath());
        changeOtherParams(serviceConfig);
        segmentExtractorService.updateParams();
        markerDao = WorkServiceFactory.createMarkerDao();
        markerService = MarkerServiceFactory.createMarkerService();
    }
        
    protected MarkerSetHolder findMarkerSetHolderByWav(File aWavFile){
        File markerFile = new File(aWavFile.getParentFile().getAbsoluteFile(),
                FileUtils.replaceExtention(aWavFile, ".mspnt.xml"));
        MarkerSetHolder markers = getMarkerDao().read(markerFile);      
        if(markers == null ){
        	markerFile = new File(aWavFile.getParentFile().getAbsoluteFile(),
                    FileUtils.replaceExtention(aWavFile, ".TextGrid"));
        	markers = getMarkerDao().read(markerFile);
        }
        org.spantus.utils.Assert.isTrue(markers!=null, "cannot be found markers for " + aWavFile);
        return markers;
    }

    /**
     *
     * @param config
     */
    protected void changeOtherParams(SegmentExtractorServiceConfig config) {
        // for other things in child classes
    }

    public File getWavFile() {
        return wavFile;
    }

    public SegmentExtractorServiceImpl getSegmentExtractorService() {
        return segmentExtractorService;
    }

    public void setSegmentExtractorService(
            SegmentExtractorServiceImpl segmentExtractorService) {
        this.segmentExtractorService = segmentExtractorService;
    }

    public File getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(File repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public void setWavFile(File wavFile) {
        this.wavFile = wavFile;
    }
    public File getRepositoryPathRoot() {
        return repositoryPathRoot;
    }

    public void setRepositoryPathRoot(File repositoryPathRoot) {
        this.repositoryPathRoot = repositoryPathRoot;
    }
    public MarkerDao getMarkerDao() {
        return markerDao;
    }
    public IMarkerService getMarkerService() {
        return markerService;
    }
}
