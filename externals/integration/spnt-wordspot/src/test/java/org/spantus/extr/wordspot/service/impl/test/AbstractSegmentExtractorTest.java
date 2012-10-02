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
    private File repositoryPathRoot = new File("../../../data");
    private File wavFile = new File(repositoryPathRoot, "text1-8000.wav");
    private File repositoryPath = new File(repositoryPathRoot,"CORPUS/phone");
    private MarkerDao markerDao;
    private IMarkerService markerService;
    protected SegmentExtractorServiceConfig serviceConfig = new SegmentExtractorServiceConfig();
        



    @Before
    public void setUp() throws Exception {
        Assert.assertTrue("repositoryPath exists", getRepositoryPath().exists());
        Assert.assertTrue("repositoryPath is directory", getRepositoryPath().isDirectory());
        Assert.assertTrue("wavFile not exists", getWavFile().exists());
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
